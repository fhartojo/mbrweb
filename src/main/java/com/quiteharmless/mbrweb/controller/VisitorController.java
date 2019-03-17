package com.quiteharmless.mbrweb.controller;

import java.util.List;

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
public class VisitorController {

	@Autowired
	private IVisitorModelService visitorModelService;

	private static final Logger log = LogManager.getLogger(VisitorController.class);

	@RequestMapping(value="/api/visitor/{id}", method=RequestMethod.GET)
	public @ResponseBody Visitor getVisitor(@PathVariable(value="id") String id) {
		log.debug("id:  " + id);

		Visitor visitor = visitorModelService.getVisitor(id);

		return visitor;
	}

	@RequestMapping(value="/api/visitors", method=RequestMethod.GET)
	public @ResponseBody List<Visitor> getVisitors() {
		List<Visitor> visitors = visitorModelService.getVisitors();

		return visitors;
	}
}
