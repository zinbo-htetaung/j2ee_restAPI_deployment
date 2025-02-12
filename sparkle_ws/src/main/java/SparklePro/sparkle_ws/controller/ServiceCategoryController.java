package SparklePro.sparkle_ws.controller;

import SparklePro.sparkle_ws.dbaccess.*;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("services") //*A. http://localhost:8080/serviceCategories //using the same name for all HTTP methods
public class ServiceCategoryController {

	@RequestMapping(method = RequestMethod.GET, path = "/getServiceCategory/{serviceCategoryId}") // use with (*A)
	public ServiceCategory getUser(@PathVariable("serviceCategoryId") int serviceCategoryId) {
		ServiceCategory serviceCategory = new ServiceCategory();
		try {
			ServiceCategoryDAO db = new ServiceCategoryDAO();
			serviceCategory = db.getServiceCategoryById(serviceCategoryId);
		} catch (Exception e) {
			System.out.println("Error :" + e);
		}
		return serviceCategory;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getAllServiceCategories")
	public ArrayList<ServiceCategory> getAllServiceCategories() {
		ArrayList<ServiceCategory> MyList = null;
		try {
			ServiceCategoryDAO db = new ServiceCategoryDAO();
			MyList = (ArrayList<ServiceCategory>) db.getAllServiceCategories();
		} catch (Exception e) {
			System.out.println("Error :" + e);
		}
		return MyList;
	}

	@RequestMapping(path = "/createServiceCategory", consumes = "application/json", method = RequestMethod.POST)
	public int createServiceCategory(@RequestBody ServiceCategory serviceCategory) {
		int rec = 0;
		try {
			ServiceCategoryDAO db = new ServiceCategoryDAO();
			rec = db.createServiceCategory(serviceCategory);
			System.out.print("...done create serviceCategory.." + rec);
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return rec;
	}

	@RequestMapping(path = "/updateServiceCategory/{serviceCategoryId}", consumes = "application/json", method = RequestMethod.PUT)
	public int updateServiceCategory(@PathVariable int serviceCategoryId, @RequestBody ServiceCategory serviceCategory) {
		int rec = 0;
		try {
			ServiceCategoryDAO db = new ServiceCategoryDAO();
			rec = db.updateServiceCategory(serviceCategoryId, serviceCategory);
			System.out.print("...in serviceContorller-done update serviceCategory..." + rec);
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return rec;
	}

	@RequestMapping(path = "/deleteServiceCategory/{serviceCategoryId}", method = RequestMethod.DELETE)
	public int deleteServiceCategory(@PathVariable int serviceCategoryId) {
		int rec = 0;
		try {
			ServiceCategoryDAO db = new ServiceCategoryDAO();
			rec = db.deleteServiceCategory(serviceCategoryId);
			System.out.print("...in serviceController-done deleting serviceCategory..." + rec);
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return rec;
	}
}
