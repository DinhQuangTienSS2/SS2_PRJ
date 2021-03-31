package vn.com.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.util.Tuple;
import vn.com.salary.Model.*;

/**
 * Represents a student. The student ID is auto-incremented from the current
 * year.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class Employee {
	public static final String E_id = "id";
	public static final String E_name = "name";
	public static final String E_gender = "gender";
	public static final String E_dob = "dob";
	public static final String E_email = "email";
	public static final String E_phone = "phone";
	public static final String E_address = "address";
	public static final String E_salary = "salary";
	public static final String E_hireDate = "hireDate";
	public static final String E_department = "department";
	public static final String E_job = "job";
	public static final String E_Fines = "Fines";
	
	
	public static final String E_rptStudentByName = "rptStudentByName";
	public static final String E_rptStudentByCity = "rptStudentByCity";

	// attributes of students
	@DAttr(name = E_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of student id
	private static int idCounter = 0;

	@DAttr(name = E_name, type = Type.String, length = 30, optional = false, cid = true)
	private String name;

	@DAttr(name = E_gender, type = Type.Domain, length = 10, optional = false)
	private Gender gender;

	@DAttr(name = E_dob, type = Type.Date, length = 15, optional = false)
	private Date dob;

	@DAttr(name = E_email, type = Type.String, length = 30, optional = false)
	private String email;

	@DAttr(name = E_phone, type = Type.String, length = 30, optional = false)
	private String phone;

	@DAttr(name = E_address, type = Type.Domain, length = 20, optional = true)
	@DAssoc(ascName = "employee-has-address", role = "employee", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Address.class, cardMin = 1, cardMax = 1))
	private Address address;

	@DAttr(name = E_salary, type = Type.Double, optional = false)
	private Double salary;

	@DAttr(name = E_hireDate, type = Type.Date, length = 15, optional = false)
	private Date hireDate;

	@DAttr(name = E_department, type = Type.Domain, length = 6)
	@DAssoc(ascName = "department-has-employees", role = "employees", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Department.class, cardMin = 1, cardMax = 1))
	private Department department;

	@DAttr(name = E_job, type = Type.Domain, length = 6)
	@DAssoc(ascName = "job-has-employees", role = "employees", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Department.class, cardMin = 1, cardMax = 1))
	private Job job;

	// Collection of Fine
	@DAttr(name = E_Fines, type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = Fine.class))
	@DAssoc(ascName = "employee-has-fines", role = "employee", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Fine.class, cardMin = 0, cardMax = 30))
	private Collection<Fine> Fines;

	// derived
	private int FineCount;

	// constructor methods
	// for creating in the application
	// without SClass
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public Employee(@AttrRef("name") String name, @AttrRef("gender") Gender gender, @AttrRef("dob") Date dob,
			@AttrRef("email") String email, @AttrRef("phone") String phone, @AttrRef("address") Address address,
			@AttrRef("salary") Double salary, @AttrRef("hireDate") Date hireDate) {
		this(null, name, gender, dob, email, phone, address, salary, hireDate, null, null);
	}

	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Employee(@AttrRef("name") String name, @AttrRef("gender") Gender gender, @AttrRef("dob") Date dob,
			@AttrRef("email") String email, @AttrRef("phone") String phone, @AttrRef("address") Address address,
			@AttrRef("salary") Double salary, @AttrRef("hireDate") Date hireDate,
			@AttrRef("department") Department department, @AttrRef("job") Job job) {
		this(null, name, gender, dob, email, phone, address, salary, hireDate, department, job);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Employee(@AttrRef("id") String id, @AttrRef("name") String name, @AttrRef("gender") Gender gender,
			@AttrRef("dob") Date dob, @AttrRef("email") String email, @AttrRef("phone") String phone,
			@AttrRef("address") Address address, @AttrRef("salary") Double salary, @AttrRef("hireDate") Date hireDate,
			@AttrRef("department") Department department, @AttrRef("job") Job job) throws ConstraintViolationException {
		// generate an id
		this.id = nextID(id);

		// assign other values
		this.name = name;
		this.gender = gender;
		this.dob = dob;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.salary = salary;
		this.hireDate = hireDate;
		this.department = department;
	    this.job = job;
		
    	Fines = new ArrayList<>();
		FineCount = 0;
	}

	// setter methods
	public void setName(String name) {
		this.name = name;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setDob(Date dob) throws ConstraintViolationException {
		// additional validation on dob
		if (dob.before(DToolkit.MIN_DOB)) {
			throw new ConstraintViolationException(DExCode.INVALID_DOB, dob);
		}

		this.dob = dob;
	}

	public void setEmail(String email) throws ConstraintViolationException {
		if (email.indexOf("@") < 0) {
			throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
					new Object[] { "'" + email + "' (does not have '@') " });
		}
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public void setHireDate(Date hireDate) throws ConstraintViolationException {
		// additional validation on dob
		if (hireDate.before(DToolkit.MIN_DOB)) {
			throw new ConstraintViolationException(DExCode.INVALID_DOB, hireDate);
		}

		this.hireDate = hireDate;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	// v2.7.3
	public void setNewAddress(Address address) {
		// change this invocation if need to perform other tasks (e.g. updating value of
		// a derived attribtes)
		setAddress(address);
	}

	public void setNewDepartment(Department department) {
		// change this invocation if need to perform other tasks (e.g. updating value of
		// a derived attribtes)
		setDepartment(department);
	}

	public void setNewJob(Job job) {
		// change this invocation if need to perform other tasks (e.g. updating value of
		// a derived attribtes)
		setJob(job);
	}

	// getter methods
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Gender getGender() {
		return gender;
	}

	public Date getDob() {
		return dob;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public Address getAddress() {
		return address;
	}

	public Double getSalary() {
		return salary;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public Department getDepartment() {
		return department;
	}

	public Job getJob() {
		return job;
	}

	public Collection<Fine> getFines() {
		return Fines;
	}

	public int getFineCount() {
		return FineCount;
	}

	public void setFineCount(int fineCount) {
		FineCount = fineCount;
	}

	/**
	 * @effects returns <code>Student(id,name,dob,address,email)</code>.
	 */
//	public String toString(boolean full) {
//		if (full)
//			return "Student(" + id + "," + name + "," + gender + ", " + dob + "," + address + "," + email
//					+ ((sclass != null) ? "," + sclass.getName() : "") + ")";
//		else
//			return "Student(" + id + ")";
//	}

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
		Employee other = (Employee) obj;
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
	// only need to do this for reflexive association: @MemberRef(name="Fines")
	public boolean addFine(Fine e) {
		if (!Fines.contains(e))
			Fines.add(e);

		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewFine(Fine e) {
		Fines.add(e);

		FineCount++;

		// v2.6.4.b

		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	// @MemberRef(name="Fines")
	public boolean addFine(Collection<Fine> enrols) {
		boolean added = false;
		for (Fine e : enrols) {
			if (!Fines.contains(e)) {
				if (!added)
					added = true;
				Fines.add(e);
			}
		}

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewFine(Collection<Fine> enrols) {
		Fines.addAll(enrols);
		FineCount += enrols.size();

		// v2.6.4.b

		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	// @MemberRef(name="Fines")
	public boolean removeFine(Fine e) {
		boolean removed = Fines.remove(e);

		if (removed) {
			FineCount--;

		}
		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkUpdater)
	// @MemberRef(name="Fines")
	public boolean updateFine(Fine e) throws IllegalStateException {
		// recompute using just the affected Fine

		// no other attributes changed
		return true;
	}

	public void setFines(Collection<Fine> en) {
		this.Fines = en;
		FineCount = en.size();

		// v2.6.4.b

	}

}