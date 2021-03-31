package vn.com.model;

import java.util.ArrayList;
import java.util.Collection;

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

/**
 * Represents a employee class.
 * 
 * @author dmle
 *
 */
@DClass(schema = "courseman")
public class Department {
	@DAttr(name = "id", id = true, auto = true, length = 6, mutable = false, type = Type.Integer)
	private int id;
	private static int idCounter;

	// candidate identifier
	@DAttr(name = "name", length = 20, type = Type.String, optional = false, cid = true)
	private String name;

//	@DAttr(name = "manager", type = Type.Domain, serialisable = false)
//	@DAssoc(ascName = "department-has-manager", role = "department", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Employee.class, cardMin = 1, cardMax = 1))
//	private Employee manager;

	@DAttr(name = "employees", type = Type.Collection, serialisable = false, optional = false, filter = @Select(clazz = Employee.class))
	// , attributes= {Employee.A_name, Employee.A_id, SClass.S_id}
	@DAssoc(ascName = "department-has-employees", role = "department", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Employee.class, cardMin = 1, cardMax = 25))
	private Collection<Employee> employees;

	// derived attributes
	private int employeesCount;
	

	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public Department(@AttrRef("name") String name) {
		//, @AttrRef("manager") Employee manager
		this(null, name);
	}

	// constructor to create objects from data source
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Department(@AttrRef("id") Integer id, @AttrRef("name") String name) {
		//, @AttrRef("manager") Employee manager
		this.id = nextID(id);
		this.name = name;
		//this.manager = manager;

		employees = new ArrayList<>();
		employeesCount = 0;
	}

	@DOpt(type = DOpt.Type.Setter)
	public void setName(String name) {
		this.name = name;
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
	public String getName() {
		return name;
	}

	@DOpt(type = DOpt.Type.Getter)
	public Collection<Employee> getEmployees() {
		return employees;
	}

	@DOpt(type = DOpt.Type.Getter)
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SClass(" + getId() + "," + getName() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Department other = (Department) obj;
		if (id != other.id)
			return false;
		return true;
	}

	private static int nextID(Integer currID) {
		if (currID == null) {
			idCounter++;
			return idCounter;
		} else {
			int num = currID.intValue();
			if (num > idCounter)
				idCounter = num;

			return currID;
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
			if (attrib.name().equals("id")) {
				int maxIdVal = (Integer) maxVal;
				if (maxIdVal > idCounter)
					idCounter = maxIdVal;
			}
		}
	}
	
//	public void setManager(Employee manager) {
//		this.manager = manager;
//	}
//	
//	public void setNewManager(Employee manager) {
//		// change this invocation if need to perform other tasks (e.g. updating value of
//		// a derived attribtes)
//		setManager(manager);
//	}
//	
//	public Employee getManager() {
//		return manager;
//	}
	
}
