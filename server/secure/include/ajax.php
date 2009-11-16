<?

	class Response {
		private $responseA;

		function __construct($tCmd) {
			$this->responseA = Array();
			$this->responseA['command'] = $tCmd;
			$this->setStatus();
		}

		function set($tData) {
			if (is_array($tData) && count($tData) > 0) {
				foreach($tData as $k => $v) {
					$this->responseA[$k] = $v;
				}
			}
		}
		function setStatus($tStatus = '', $tMsg = '') {
			$this->responseA['status'] = $tStatus;
			$this->responseA['msg']	= $tMsg;
		}
		function get() {
			return json_encode($this->responseA);
		}
	}

	function execute($tCMD) {
		$outputA = array();
		exec("$tCMD 2>&1", $outputA);
		return implode("\n", $outputA);
	}

?>