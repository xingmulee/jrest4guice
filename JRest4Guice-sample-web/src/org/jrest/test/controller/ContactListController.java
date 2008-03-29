package org.jrest.test.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrest.HttpResult;
import org.jrest.annotation.FirstResult;
import org.jrest.annotation.HttpMethod;
import org.jrest.annotation.HttpMethodType;
import org.jrest.annotation.MaxResults;
import org.jrest.annotation.Restful;
import org.jrest.context.ModelMap;
import org.jrest.test.service.ContactService;

import com.google.inject.Inject;

@Restful(uri = "/contacts")
public class ContactListController {
	@Inject
	protected ModelMap modelMap;

	@Inject
	protected HttpServletRequest request;

	@Inject
	protected HttpServletResponse response;

	@Inject
	private ContactService service;

	@HttpMethod(type = HttpMethodType.GET)
	public String getContact(@FirstResult int first, @MaxResults int max) {
		try {
			List contacts = this.service.listContacts(first, max);
			String json = HttpResult.createSuccessfulHttpResult(contacts).toJson();
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return HttpResult.createFailedHttpResult(e.getCause().getClass().getName(),e.getMessage()).toJson();
		}
	}
}