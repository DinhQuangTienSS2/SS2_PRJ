package vn.com.software;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import domainapp.basics.core.dodm.qrm.QRM;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.Oid;
import domainapp.basics.model.query.Expression.Op;
import domainapp.basics.model.query.Query;
import domainapp.basics.model.query.QueryToolKit;
import domainapp.basics.util.Toolkit;
import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.model.City;
import vn.com.model.Gender;
import vn.com.model.Employee;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DomMainEmployee {
  
  public static void main(String[] args) {
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
    
    // this should be run subsequent times
    sw.init();
    
    try {
      // register a domain model fragment concerning Employee
      Class[] domFrag = {
          Employee.class
      };
      sw.addClasses(domFrag);
      
      // create some Employee objects
      createEmployee(sw);
      
      // read object:
//      querySimple(sw, Employee.class, Employee.A_id, Op.EQ, "S2020");
      
      queryEmployees(sw);
      
      // display the domain model and its instances
//      boolean displayFqn = false;
//      sw.printDomainModel(displayFqn);
      
      // check that a new object is in the object pool
//      sw.printObjectPool(Employee.class);

      // check that object is in the database by printing data in the database
      sw.printObjectDB(Employee.class);
      
      // update object:
//      updateObject(sw, "S2020");
      
      // delete object:
//      deleteObject(sw, "S2020");
    } catch (DataSourceException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * @effects 
   * 
   * @version 
   * @throws DataSourceException 
   * @throws NotPossibleException 
   * 
   */
  private static void queryEmployees(DomSoftware sw) throws NotPossibleException, DataSourceException {
    Map<Oid, Employee> result = queryEmployeesByNamePattern("Du");
    if (result != null) {
      sw.printObjects(Employee.class, result.values());
    } else {
      System.out.println("No match");
    }    
    
    result = queryEmployeesByCity("Hanoi");
    if (result != null) {
      sw.printObjects(Employee.class, result.values());
    } else {
      System.out.println("No match");
    }        
  }
  
  /**
   * @return 
   * @effects 
   * 
   */
  private static <T> Collection<T> querySimple(DomSoftware sw, Class<T> cls, 
      String attribName, Op op, String val) throws NotPossibleException, DataSourceException {
    
    Collection<T> objects = sw.retrieveObjects(cls, attribName, op, val);
    sw.printObjects(cls, objects);
    return objects;
  }
  
  /**
   * @effects 
   * 
   */
  private static Map<Oid, Employee> queryEmployeesByNamePattern(String name) throws NotPossibleException, DataSourceException {
    QRM qrm = QRM.getInstance();
    // create query
    String namePattern = "%"+name+"%";
    Query q = QueryToolKit.createSearchQuery(
        qrm.getDsm(), 
        Employee.class, 
        new String[] {Employee.E_name}, 
        new Op[] {Op.MATCH}, new Object[] {namePattern});
    
    System.out.printf("Querying Employees with name matching '%s'%n", namePattern);
    Map<Oid, Employee> result = qrm.getDom().retrieveObjects(Employee.class, q);
    return result; 
  }

  private static Map<Oid, Employee> queryEmployeesByCity(String cityName) throws NotPossibleException, DataSourceException {
    QRM qrm = QRM.getInstance();
    // create query
    Query q = QueryToolKit.createSimpleJoinQuery(qrm.getDsm(), 
        Employee.class, City.class,  
        Employee.E_address, 
        City.C_name, 
        Op.MATCH, 
        "%"+cityName+"%");
    
    System.out.printf("Querying Employees whose address is City(name='%s')%n", cityName);
    Map<Oid, Employee> result = qrm.getDom().retrieveObjects(Employee.class, q);
    return result;
  }
  

  /**
   * @effects 
   * 
   */
  private static void createEmployee(DomSoftware sw) throws NotFoundException, DataSourceException {
    // get a city object
    City city = sw.retrieveObjectById(City.class, 2);
    // create a Employee
    Date dob;
    //dob = Toolkit.getDateZeroTime(1, 1, 1970);
    
    // another method of creating Date
    dob = createDateFromString("1/1/1970");
    
//    sw.addObject(Employee.class,
    //    new Employee("tien",Gender.Female,dob,"tt@.com",00,"hanoio",32.0,null)
    //    );    
  }

  /**
   * @effects 
   *  return a Date object whose string representation is <tt>dateStr</tt>.
   *  If dateStr is invalid
   *    return null
   */
  private static Date createDateFromString(String dateStr) {
    DateFormat dformat = new SimpleDateFormat("dd/MM/yyyy");
    Date dt = null;
    try {
      dt = dformat.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    
    return dt;
  }

  /**
   * @effects 
   * 
   */
  private static void updateObject(DomSoftware sw, Object id) throws NotFoundException, DataSourceException {
    Employee s = sw.retrieveObjectById(Employee.class, id);
    if (s != null) {
      System.out.printf("Updating object%n%s%n", s);
      sw.updateObject(Employee.class, s, 
          new String[] {
              Employee.E_email, Employee.E_address},
          new Object[] {
              "leminhduc@gmail.com",
              sw.retrieveObjectById(City.class, 2)
          });
      System.out.printf("... after:%n%s%n", s);
    }    
  }

  /**
   * @effects 
   * 
   * @version 
   * @param sw 
   * 
   */
  private static void deleteObject(DomSoftware sw, Object id) throws NotFoundException, DataSourceException {
    Employee s = sw.retrieveObjectById(Employee.class, id);
    if (s != null) {
      System.out.printf("Deleting object%n%s%n", s);
      sw.deleteObject(s, Employee.class);
      sw.printObjectDB(Employee.class);
    }    
  }
}