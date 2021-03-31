package vn.com.software;

import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.model.Address;
import vn.com.model.City;
import vn.com.model.Department;
import vn.com.model.Employee;
import vn.com.model.Job;
import vn.com.salary.Model.Fine;


/**
 * @overview 
 *  Create and run a UI-based {@link DomSoftware} for a pre-defined model.  
 *  
 * @author dmle
 */
public class Main {
  
  // 1. initialise the model
  static final Class[] model = {
  
      City.class, 
      Address.class,
      Job.class,
      Department.class,
      Employee.class,
       Fine.class,

  };
  
  /**
   * @effects 
   *  create and run a UI-based {@link DomSoftware} for a pre-defined model. 
   */
  public static void main(String[] args){
    // 2. create UI software
    DomSoftware sw = SoftwareFactory.createUIDomSoftware();
    
    // 3. run
    // create in memory configuration
    System.setProperty("domainapp.setup.SerialiseConfiguration", "false");
    
    // 3. run it
    try {
      sw.run(model);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }   
  }

}
