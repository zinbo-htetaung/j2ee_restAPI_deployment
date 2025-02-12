package SparklePro.sparkle_ws.controller;

import SparklePro.sparkle_ws.dbaccess.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("services") //*A. http://localhost:8080/services //using the same name for all HTTP methods
public class ServiceController {

	@RequestMapping(method = RequestMethod.GET, path = "/getService/{serviceId}") // use with (*A)
	public Service getService(@PathVariable("serviceId") int serviceId) {
		Service service = new Service();
		try {
			ServiceDAO db = new ServiceDAO();
			service = db.getServiceById(serviceId);
		} catch (Exception e) {
			System.out.println("Error :" + e);
		}
		return service;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getFilteredServices")
	public List<Service> getFilteredServices(@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer categoryId, @RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice) {
		List<Service> services = new ArrayList<>();
		try {
			ServiceDAO db = new ServiceDAO();
			services = db.getFilteredServices(name, categoryId, minPrice, maxPrice);
		} catch (Exception e) {
			System.out.println("Error fetching filtered services: " + e);
		}
		return services;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getAllServices")
	public ArrayList<Service> getAllServices() {
		ArrayList<Service> MyList = null;
		try {
			ServiceDAO db = new ServiceDAO();
			MyList = (ArrayList<Service>) db.getAllServices();
		} catch (Exception e) {
			System.out.println("Error :" + e);
		}
		return MyList;
	}

	@RequestMapping(path = "/createService", consumes = "application/json", method = RequestMethod.POST)
	public int createService(@RequestBody Service service) {
		int rec = 0;
		try {
			ServiceDAO db = new ServiceDAO();
			rec = db.createService(service);
			System.out.print("...done create service.." + rec);
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return rec;
	}

	@RequestMapping(path = "/updateService/{serviceId}", consumes = "application/json", method = RequestMethod.PUT)
	public int updateService(@PathVariable int serviceId, @RequestBody Service service) {
		int rec = 0;
		try {
			ServiceDAO db = new ServiceDAO();
			rec = db.updateService(serviceId, service);
			System.out.print("...in serviceContorller-done update service..." + rec);
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return rec;
	}

	@RequestMapping(path = "/deleteService/{serviceId}", method = RequestMethod.DELETE)
	public int deleteService(@PathVariable int serviceId) {
		int rec = 0;
		try {
			ServiceDAO db = new ServiceDAO();
			rec = db.deleteService(serviceId);
			System.out.print("...in serviceController-done deleting service..." + rec);
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return rec;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getPopularServices")
	public Map<String, Object> getPopularServices() {
		ServiceDAO db = new ServiceDAO();
		return db.getPopularServices(); // ✅ Returns both popular services & booking counts
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getAverageRating/{serviceId}")
	public double getAverageRating(@PathVariable("serviceId") int serviceId) {
		ServiceDAO db = new ServiceDAO();
		return db.getAverageRating(serviceId);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getAllReviews/{serviceId}")
	public List<Map<String, Object>> getAllReviews(@PathVariable("serviceId") int serviceId) {
		ServiceDAO db = new ServiceDAO();
		return db.getAllReviewsForService(serviceId);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getServicesSortedByRating")
	public Map<String, Object> getServicesSortedByRating(@RequestParam(defaultValue = "desc") String sortOrder) {
		try {
			ServiceDAO db = new ServiceDAO();
			boolean ascending = "asc".equalsIgnoreCase(sortOrder);
			return db.getServicesFilterByRating(ascending);
		} catch (Exception e) {
			System.out.println("Error fetching services by rating: " + e);
			return Map.of();
		}
	}
}
