package com.quiteharmless.mbrweb.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quiteharmless.mbrweb.bo.Visitor;
import com.quiteharmless.mbrweb.model.IVisitorModelService;

@Controller
public class LookupController {

	@Autowired
	private IVisitorModelService visitorModelService;

	private static final Logger log = LogManager.getLogger(LookupController.class);

	@RequestMapping(value="/lookup/{lookupId}", method=RequestMethod.GET)
	public @ResponseBody Visitor getVisitorInfo(@PathVariable(value="lookupId") String lookupId) {
		log.debug("lookupId:  " + lookupId);

		Visitor visitor = visitorModelService.getVisitorInfo(lookupId);

		return visitor;
	}
}
