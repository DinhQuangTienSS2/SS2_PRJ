package vn.com.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.util.Tuple;

/**
 * Represents a student. The student ID is auto-incremented from the current
 * year.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "employeemanagement")
public class Job {
	public static final String J_id = "id";
	public static final String J_jobTitle = "job_title";
	public static final String J_minSalary = "min_salary";
	public static final String J_maxSalary = "max_salary";

	// attributes of job
	@DAttr(name = J_id, id = true, type = Type.String, auto = true, mutable = false, optional = false)
	private String id;
	// static variable to keep track of student id
	private static int idCounter = 0;

	@DAttr(name = J_jobTitle, type = Type.String, optional = false, cid = true)
	private String job_title;

	@DAttr(name = J_minSalary, type = Type.String, optional = false, cid = true)
	private String min_salary;

	@DAttr(name = J_maxSalary, type = Type.String, optional = false, cid = true)
	private String max_salary;
	
	@DAttr(name = "employees", type = Type.Collection, serialisable = false, optional = false, filter = @Select(clazz = Employee.class))
	// , attributes= {Employee.A_name, Employee.A_id, SClass.S_id}
	@DAssoc(ascName = "department-has-employees", role = "department", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Employee.class, cardMin = 1, cardMax = 25))
	private Collection<Employee> employees;

	// derived attributes
	private int employeesCount;

	// constructor methods
	// for creating in the application
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public Job(@AttrRef("job_title") String job_title, @AttrRef("min_salary") String min_salary,
			@AttrRef("max_salary") String max_salary) {
		this(null, job_title, min_salary, max_salary);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Job(@AttrRef("id") String id, @AttrRef("job_title") String job_title, @AttrRef("min_salary") String min_salary,
			@AttrRef("max_salary") String max_salary)
			throws ConstraintViolationException {
		// generate an id
		this.id = nextID(id);
		this.job_title = job_title;
		this.min_salary = min_salary;
		this.max_salary = max_salary;
		
		employees = new ArrayList<>();
		employeesCount = 0;
	}

	// setter methods
	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}

	public void setMin_salary(String min_salary) {
		this.min_salary = min_salary;
	}
	
	public void setMax_salary(String max_salary) {
		this.min_salary = max_salary;
	}

	// v2.7.3
//	public void setNewJob_title(String job_title) {
//		// change this invocation if need to perform other tasks (e.g. updating value of
//		// a derived attribtes)
//		setJob_title(job_title);
//	}

	// getter methods
	public String getId() {
		return id;
	}

	public String getJob_title() {
		return job_title;
	}

	public String getMin_salary() {
		return min_salary;
	}
	
	public String getMax_salary() {
		return max_salary;
	}

	// override toString
	/**
	 * @effects returns <code>this.id</code>
	 */
	@Override
	public String toString() {
		return toString(true);
	}

	/**
	 * @effects returns <code>Student(id,address,dob,city,email)</code>.
	 */
	public String toString(boolean full) {
		if (full)
			return "Student(" + id + "," + job_title + ", " + min_salary + ", " + max_salary+ ")";
		else
			return "Student(" + id + ")";
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
		Job other = (Job) obj;
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
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { id });
			}

			if (num > idCounter) {
				idCounter = num;
			}

			return id;
		}
	}

	/**
	 * @requires minVal != null /\ maxVal != null
	 * @effects update the auto-generated value of attribute <tt>attrib</tt>,
	 *          specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	 */
	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			// TODO: update this for the correct attribute if there are more than one auto
			// attributes of this class

			String maxId = (String) maxVal;

			try {
				int maxIdNum = Integer.parseInt(maxId.substring(1));

				if (maxIdNum > idCounter) // extra check
					idCounter = maxIdNum;

			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { maxId });
			}
		}
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="employees")
	public boolean addEmployee(Employee s) {
		if (!this.employees.contains(s)) {
			employees.add(s);
		}

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewEmployee(Employee s) {
		employees.add(s);
		employeesCount++;

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addEmployee(Collection<Employee> employees) {
		for (Employee s : employees) {
			if (!this.employees.contains(s)) {
				this.employees.add(s);
			}
		}

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewEmployee(Collection<Employee> employees) {
		this.employees.addAll(employees);
		employeesCount += employees.size();

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="employees")
	public boolean removeEmployee(Employee s) {
		boolean removed = employees.remove(s);

		if (removed) {
			employeesCount--;
		}

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.Setter)
	public void setEmployees(Collection<Employee> employees) {
		this.employees = employees;

		employeesCount = employees.size();
	}

	/**
	 * @effects return <tt>employeesCount</tt>
	 */
	@DOpt(type = DOpt.Type.LinkCountGetter)
	public Integer getEmployeesCount() {
		return employeesCount;
	}

	@DOpt(type = DOpt.Type.LinkCountSetter)
	public void setEmployeesCount(int count) {
		employeesCount = count;
	}
	
	@DOpt(type = DOpt.Type.Getter)
	public Collection<Employee> getEmployees() {
		return employees;
	}
}
