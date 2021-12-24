package com.quiteharmless.mbrweb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quiteharmless.mbrweb.bo.DataLoadStatus;
import com.quiteharmless.mbrweb.model.IDataLoaderModelService;

@Controller
public class DataLoaderController {

	@Autowired
	private IDataLoaderModelService dataLoaderModelService;

	private static final Logger log = LoggerFactory.getLogger(DataLoaderController.class);

	@RequestMapping(value="/api/loadMembershipData", method=RequestMethod.GET)
	public @ResponseBody DataLoadStatus loadMembershipData() {
		DataLoadStatus status = new DataLoadStatus();

		status.setOk(true);
		status.setMessage("");

		try {
			this.dataLoaderModelService.loadMembershipData();
		} catch (Exception e) {
			log.error("Exception", e);

			status.setOk(false);
			status.setMessage(e.getMessage());
		}

		return status;
	}
}
