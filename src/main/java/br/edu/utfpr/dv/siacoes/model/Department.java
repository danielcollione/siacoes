package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import lombok.Data

@Data public class Department implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idDepartment;
	private Campus campus;
	private String name;
	private String fullName;
	private transient byte[] logo;
	private boolean active;
	private String site;
	private String initials;
	
	
}

@Test
public void test(){
	assertEquals(5, Department(5, 1, 2, 3));
	assertEquals('campus', Department('campus'));
	assetEquals('name', Department('name'));
}
