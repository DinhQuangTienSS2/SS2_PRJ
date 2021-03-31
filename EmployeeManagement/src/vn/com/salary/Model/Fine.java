package vn.com.salary.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import vn.com.model.City;
import vn.com.model.Employee;
import vn.com.model.Gender;

public class Fine {
	public static final String E_name = "name";
	public static final String E_id = "id";
	public static final String E_day = "day";
	public static final String E_reason = "reason";
	public static final String E_AmountOfFine = "AmountOfFine";
	public static final String E_Employee = "employee";
	// attributes of students
	@DAttr(name = E_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of student id
	private static int idCounter = 0;

	@DAttr(name = E_name, type = Type.String, length = 30, optional = false, cid = true)
	private String name;

	  @DAttr(name = E_AmountOfFine, type = Type.Double, length = 9, optional = true, min = 0.0)
	  private Double AmountOfFine;
	  
	@DAttr(name = E_day, type = Type.Date, length = 15, optional = false)
	private Date day;

	@DAttr(name = E_reason, type = Type.String, length = 30, optional = false)
	private String reason;
	
	
	  @DAttr(name = E_Employee, type = Type.Domain, length = 5, optional = false)
	  @DAssoc(ascName = "employee-has-fines", role = "fine", 
	    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
	    associate = @Associate(type =Employee.class, cardMin = 1, cardMax = 1), dependsOn = true)
	  private Employee employee;

	
	
	 @DOpt(type=DOpt.Type.ObjectFormConstructor)
	  @DOpt(type=DOpt.Type.RequiredConstructor)
	  public Fine(@AttrRef("name") String name, 
			  @AttrRef("amountOfFine") Double amountOfFine,
	      @AttrRef("day") Date day, 
	      @AttrRef("reason") String reason) {
	    this(null, name,amountOfFine, day, reason);
	  }
	
	 
	  @DOpt(type=DOpt.Type.DataSourceConstructor)
	  public Fine(@AttrRef("id") String id, 
			  @AttrRef("name") String name, 
			  @AttrRef("AmountOfFine") Double AmountOfFine, 
	      @AttrRef("dob") Date day, 
	      @AttrRef("reason") String reason) 
	  throws ConstraintViolationException {
	    // generate an id
	    this.id = nextID(id);

	    // assign other values
	    this.name = name;
	    this.AmountOfFine=AmountOfFine;
	    this.day = day;
	    this.reason=reason;

	    
	  }

	  // setter methods
	  public void setName(String name) {
	    this.name = name;
	  }
	
	  
	  

	  
	  public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Double getAmountOfFine() {
		return AmountOfFine;
	}


	public void setAmountOfFine(Double amountOfFine) {
		AmountOfFine = amountOfFine;
	}


	public Date getDay() {
		return day;
	}


	public void setDay(Date day) {
		this.day = day;
	}


	public String getReason() {
		return reason;
	}


	public void setReason(String reason) {
		this.reason = reason;
	}


	public String getName() {
		return name;
	}


	@Override
	  public String toString() {
	    return toString(true);
	  }

	  /**
	   * @effects returns <code>Student(id,name,dob,address,email)</code>.
	   */
	  public String toString(boolean full) {
	    if (full)
	      return "Fine(" + id + "," + name + "," + AmountOfFine + ", " + day + "," + reason + 
	     ")";
	    else
	      return "Fine(" + id + ")";
	  }
	  
	  @Override
	  public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    return result;
	  }

	  @Override
	  public boolean equals(Object obj) {
	    if (this == obj)
	      return true;
	    if (obj == null)
	      return false;
	    if (getClass() != obj.getClass())
	      return false;
	   Fine other = (Fine) obj;
	    if (id == null) {
	      if (other.id != null)
	        return false;
	    } else if (!id.equals(other.id))
	      return false;
	    return true;
	  }

	  // automatically generate the next student id
	  private String nextID(String id) throws ConstraintViolationException {
	    if (id == null) { // generate a new id
	      if (idCounter == 0) {
	        idCounter = Calendar.getInstance().get(Calendar.YEAR);
	      } else {
	        idCounter++;
	      }
	      return "S" + idCounter;
	    } else {
	      // update id
	      int num;
	      try {
	        num = Integer.parseInt(id.substring(1));
	      } catch (RuntimeException e) {
	        throw new ConstraintViolationException(
	            ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] { id });
	      }
	      
	      if (num > idCounter) {
	        idCounter = num;
	      }
	      
	      return id;
	    }

	  
}

}

