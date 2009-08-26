/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.google.android.odk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.javarosa.core.JavaRosaServiceProvider;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.GroupDef;
import org.javarosa.core.model.IFormElement;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.DataModelTree;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.core.services.IService;
import org.javarosa.core.services.transport.ByteArrayPayload;
import org.javarosa.core.services.transport.DataPointerPayload;
import org.javarosa.core.services.transport.IDataPayload;
import org.javarosa.core.services.transport.MultiMessagePayload;
import org.javarosa.model.xform.XFormSerializingVisitor;
import org.javarosa.xform.parse.XFormParser;

import android.content.Context;
import android.util.Log;

/**
 * Given a {@link FormDef}, enables form iteration.
 * 
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author @author Yaw Anokwa (yanokwa@gmail.com)
 */
public class FormHandler {

    public final String t = "FormHandler";

    private FormDef mForm;
    private FormIndex mCurrentIndex;
    private int mQuestionCount;
    private String mSourcePath;


    public FormHandler(FormDef formDef) {
        Log.i(t, "calling constructor");

        mForm = formDef;
        mCurrentIndex = FormIndex.createBeginningOfFormIndex();

    }


    public void initialize(Context context) {
        
        // load services
        Vector<IService> v = new Vector<IService>();
        v.add(new PropertyManager(context));
        JavaRosaServiceProvider.instance().initialize(v);
        
        // set evaluation context
        EvaluationContext ec = new EvaluationContext();
        ec.addFunctionHandler(new RegexFunction());
        mForm.setEvaluationContext(ec);
        
        // initialize form
        mForm.initialize(true);

    }


    /**
     * Attempts to save the answer 'answer' into prompt.
     * 
     * @param prompt
     * @param answer
     */
    public int saveAnswer(PromptElement prompt, IAnswerData answer, boolean validate) {
        if (!mForm.evaluateConstraint(prompt.getInstanceRef(), answer) && validate) {
            return SharedConstants.ANSWER_CONSTRAINT_VIOLATED;
        } else if (prompt.isRequired() && answer == null && validate) {
            return SharedConstants.ANSWER_REQUIRED_BUT_EMPTY;
        } else {
            mForm.setValue(answer, prompt.getInstanceRef(), prompt.getInstanceNode());
            return SharedConstants.ANSWER_OK;
        }
    }

    /**
     * Deletes the innermost group that repeats that this node belongs to.
     */
    public void deleteCurrentRepeat() {
        mCurrentIndex = mForm.deleteRepeat(mCurrentIndex);
    }


    /**
     * Returns a vector of the GroupElement hierarchy to which this question
     * belongs
     */
    private Vector<GroupElement> getGroups() {
        Vector<Integer> mult = new Vector<Integer>();
        Vector<IFormElement> elements = new Vector<IFormElement>();
        Vector<GroupElement> groups = new Vector<GroupElement>();

        getForm().collapseIndex(mCurrentIndex, new Vector<Object>(), mult, elements);
        for (int i = 0; i < elements.size(); i++) {
            IFormElement fi = elements.get(i);
            if (fi instanceof GroupDef) {
                groups.add(new GroupElement(((GroupDef) fi).getLongText(), mult.get(i).intValue(), ((GroupDef)fi).getRepeat()));
            }
        }

        return groups;
    }


    /**
     * Set the currentIndex to the specified FormIndex
     * 
     * @param newIndex
     */
    public void setFormIndex(FormIndex newIndex) {
        mCurrentIndex = newIndex;
    }


    public FormIndex getIndex() {
        return mCurrentIndex;
    }


    /**
     * Returns the prompt associated with the next relevant question or repeat.
     * For filling out forms, use this rather than nextQuestionPrompt()
     * 
     */
    public PromptElement nextPrompt() {
        nextRelevantIndex();

        while (!isEnd()) {
            Vector<IFormElement> defs = getIndexVector(mCurrentIndex);
            if (indexIsGroup(mCurrentIndex)) {
                GroupDef last = (defs.size() == 0 ? null : (GroupDef) defs.lastElement());
                
                if (last.getRepeat() && resolveReferenceForCurrentIndex() == null) {
                    return new PromptElement(getGroups());
                } else {
                    // this group doesn't repeat, so skip passed it
                }
            } else {
                // we have a question
                mQuestionCount++;
                return new PromptElement(mCurrentIndex, getForm(), getGroups());
            }
            nextRelevantIndex();
        }
        mQuestionCount++;
        // we're at the end of our form
        return null;
    }


    /**
     * returns the prompt associated with the next relevant question. This
     * should only be used when iterating through the questions as it ignores
     * any repeats
     * 
     */
    public PromptElement nextQuestionPrompt() {
        nextRelevantIndex();

        while (!isEnd()) {
            if (indexIsGroup(mCurrentIndex)) {
                nextRelevantIndex();
                continue;
            } else {
                // we have a question
                return new PromptElement(mCurrentIndex, getForm(), getGroups());
            }
        }

        return null;
    }


    public PromptElement currentPrompt() {
        if (indexIsGroup(mCurrentIndex))
            return new PromptElement(getGroups());
        else
            return new PromptElement(mCurrentIndex, getForm(), getGroups());
    }


    /**
     * returns the prompt for the previous relevant question
     * 
     */
    public PromptElement prevPrompt() {
        mQuestionCount--;
        prevQuestion();
        if (isBeginning())
            return null;
        else
            return new PromptElement(mCurrentIndex, getForm(), getGroups());
    }


    private void nextRelevantIndex() {
        do {
            mCurrentIndex = mForm.incrementIndex(mCurrentIndex);
        } while (mCurrentIndex.isInForm() && !isRelevant(mCurrentIndex));
    }


    /**
     * moves index to the previous relevant index representing a question,
     * skipping any groups
     */
    private void prevQuestion() {
        do {
            mCurrentIndex = mForm.decrementIndex(mCurrentIndex);
        } while (mCurrentIndex.isInForm() && !isRelevant(mCurrentIndex));

        // recursively skip backwards past any groups, and pop them from our stack
        if (indexIsGroup(mCurrentIndex)) {
            prevQuestion();
        }
    }


    private boolean indexIsGroup(FormIndex index) {
        Vector<IFormElement> defs = getIndexVector(index);
        IFormElement last = (defs.size() == 0 ? null : (IFormElement) defs.lastElement());
        if (last instanceof GroupDef) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isBeginning() {
        if (mCurrentIndex.isBeginningOfFormIndex())
            return true;
        else
            return false;
    }


    public boolean isEnd() {
        if (mCurrentIndex.isEndOfFormIndex())
            return true;
        else
            return false;
    }


    /**
     * returns true if the specified FormIndex should be displayed, false
     * otherwise
     * 
     * @param questionIndex
     */
    private boolean isRelevant(FormIndex questionIndex) {
        TreeReference ref = mForm.getChildInstanceRef(questionIndex);
        boolean isAskNewRepeat = false;

        Vector<IFormElement> defs = getIndexVector(questionIndex);
        IFormElement last = (defs.size() == 0 ? null : (IFormElement) defs.lastElement());
        if (last instanceof GroupDef
                && ((GroupDef) last).getRepeat()
                && mForm.getDataModel().resolveReference(mForm.getChildInstanceRef(questionIndex)) == null) {
            isAskNewRepeat = true;
        }

        boolean relevant;
        if (isAskNewRepeat) {
            relevant = mForm.canCreateRepeat(ref);
        } else {
            TreeElement node = mForm.getDataModel().resolveReference(ref);
            relevant = node.isRelevant(); // check instance flag first
        }

        if (relevant) { // if instance flag/condition says relevant, we still
            // have
            // to check the <group>/<repeat> hierarchy
            FormIndex ancestorIndex = null;
            FormIndex cur = null;
            FormIndex qcur = questionIndex;
            for (int i = 0; i < defs.size() - 1; i++) {
                FormIndex next = new FormIndex(qcur.getLocalIndex(), qcur.getInstanceIndex());
                if (ancestorIndex == null) {
                    ancestorIndex = next;
                    cur = next;
                } else {
                    cur.setNextLevel(next);
                    cur = next;
                }
                qcur = qcur.getNextLevel();

                TreeElement ancestorNode =
                        mForm.getDataModel().resolveReference(
                                mForm.getChildInstanceRef(ancestorIndex));
                if (!ancestorNode.isRelevant()) {
                    relevant = false;
                    break;
                }
            }
        }

        return relevant;
    }


    public void newRepeat() {
        mForm.createNewRepeat(mCurrentIndex);
    }


    public String getCurrentLanguage() {
        if (mForm.getLocalizer() != null) {
            return mForm.getLocalizer().getLocale();
        }
        return null;
    }


    public String[] getLanguages() {
        if (mForm.getLocalizer() != null) {
            return mForm.getLocalizer().getAvailableLocales();
        }
        return null;
    }


    public void setLanguage(String language) {
        if (mForm.getLocalizer() != null) {
            mForm.getLocalizer().setLocale(language);
        }
    }


    @SuppressWarnings("unchecked")
    public Vector<IFormElement> getIndexVector(FormIndex index) {
        return mForm.explodeIndex(index);
    }


    public FormDef getForm() {
        return mForm;
    }


    private TreeElement resolveReferenceForCurrentIndex() {
        return mForm.getDataModel().resolveReference(mForm.getChildInstanceRef(mCurrentIndex));
    }


    public float getQuestionProgress() {
        return mQuestionCount / getQuestionCount();
    }


    public int getQuestionNumber() {
        return mQuestionCount;
    }


    // TODO: These two methods are really hacky and completely inefficient. We
    // should figure out a way to keep
    // track of the number of questions we have without having to loop through
    // the entire index each time.
    public FormIndex nextIndexForCount(FormIndex i) {
        do {
            i = mForm.incrementIndex(i);
        } while (i.isInForm() && !isRelevant(i));
        return i;
    }


    public int getQuestionCount() {
        int count = 0;
        FormIndex i = FormIndex.createBeginningOfFormIndex();

        i = nextIndexForCount(i);

        while (!i.isEndOfFormIndex()) {
            if (indexIsGroup(i)) {
            } else {
                // we have a question
                count++;
            }
            i = nextIndexForCount(i);
        }

        return count + 1;
    }


    public String getFormTitle() {
        return mForm.getTitle();
    }


    public void setSourcePath(String path) {
        mSourcePath = path;
    }


    public String getSourcePath() {
        return mSourcePath;
    }


    // TODO report directory fail
    private boolean exportXMLPayload(ByteArrayPayload payload, String now) {
        InputStream is = payload.getPayloadStream();
        int len = (int) payload.getLength();

        Log.i("exportXMLPayload", Integer.toString(len));

        byte[] data = new byte[len];
        try {
            is.read(data, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String fname =
                getSourcePath().substring(getSourcePath().lastIndexOf("/") + 1,
                        getSourcePath().lastIndexOf("."));

        String dname = SharedConstants.ANSWERS_PATH + "/" + fname + "_" + now + "/";

        File dir = new File(dname);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return false;
            }
        }

        ///// BEGIN try send data to server
        HttpClient tClient = new DefaultHttpClient();
        HttpPost   tPost   = new HttpPost(SharedConstants.MOCHA_URL);
        
        try {
        	List<NameValuePair> tPairs = new ArrayList<NameValuePair>(2);   	
        	tPairs.add(new BasicNameValuePair("xml", new String(data, "UTF-8")));
        	tPost.setEntity(new UrlEncodedFormEntity(tPairs));

        	HttpResponse tResponse = tClient.execute(tPost);

			HttpEntity tEntity = tResponse.getEntity();
			//int tStatus = tResponse.getStatusLine().getStatusCode();
			InputStream tStream = tEntity.getContent();
			String tResponseText = convertStreamToString(tStream);
			tStream.close();
			if (tEntity != null) {
			    tEntity.consumeContent();
			}        			        	
        	Log.i("POSTDATA", tResponseText);
        } catch(ClientProtocolException e) {
        	Log.e("httpPost", e.getMessage());
        } catch (IOException e) {
        	Log.e("httpPost", e.getMessage());
            e.printStackTrace(); 		
        }	        
        ///// END try send data to server
        
        try {
            BufferedWriter o =
                    new BufferedWriter(new FileWriter(dname + fname + "_" + now + ".xml"));
            o.write(new String(data, "UTF-8"));
            o.flush();
            o.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }

    // ADDED BY ABE FOR HTTP POST USAGE
    private String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return sb.toString();
    } 


	private boolean exportJPGPayload(DataPointerPayload payload, String now) {
        InputStream is = payload.getPayloadStream();
        int len = (int) payload.getLength();

        byte[] data = new byte[len];
        try {
            is.read(data, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String fname =
                getSourcePath().substring(getSourcePath().lastIndexOf("/") + 1,
                        getSourcePath().lastIndexOf("."));

        String dname = SharedConstants.ANSWERS_PATH + "/" + fname + "_" + now + "/";

        File d = new File(dname);
        if (!d.exists()) {
            if (!d.mkdirs()) {
                return false;
            }
        }
        try {

            FileOutputStream o = new FileOutputStream(dname + payload.getPayloadId() + ".jpg");
            o.write(data);
            o.flush();
            o.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * Runs post processing handlers. Necessary to get end time.
     */
    public void finalizeDataModel() {
        mForm.postProcessModel();
    }
    

    public void importData(byte[] savedXML) {
        
        DataModelTree brokenTree = XFormParser.restoreDataModel(savedXML, null);
        TreeElement fixedRoot = DataModelTree.processSavedDataModel(brokenTree.getRoot(), mForm.getDataModel(), mForm);
        DataModelTree fixedTree = new DataModelTree(fixedRoot);
        mForm.setDataModel(fixedTree);
        
        }


    // should return something
    @SuppressWarnings("unchecked")
    public boolean exportData() {
        String now =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                        .format(Calendar.getInstance().getTime());
        DataModelTree instance = mForm.getDataModel();
        IDataPayload payload = null;

        try {
            payload = (new XFormSerializingVisitor()).createSerializedPayload(instance);
        } catch (IOException e) {
            Log.e(t, "Error creating serialized payload");
            return false;
        }

        switch (payload.getPayloadType()) {
            case IDataPayload.PAYLOAD_TYPE_MULTI:
                Vector<IDataPayload> payloads = ((MultiMessagePayload) payload).getPayloads();
                for (Object p : payloads) {
                    switch (((IDataPayload) p).getPayloadType()) {
                        case IDataPayload.PAYLOAD_TYPE_XML:
                            if (!exportXMLPayload((ByteArrayPayload) p, now)) {
                                return false;
                            }
                            break;
                        case IDataPayload.PAYLOAD_TYPE_JPG:
                            if (!exportJPGPayload((DataPointerPayload) p, now)) {
                                return false;
                            }
                            break;
                    }
                }
                break;
            case IDataPayload.PAYLOAD_TYPE_XML:
                if (!exportXMLPayload((ByteArrayPayload) payload, now)) {
                    return false;
                }
                break;

        }
        return true;

    }


}
