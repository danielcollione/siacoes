package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Department;

//Decorrente da análise de software feita no algoritmo DepartmentDao, creio que não há nenhuma alteração a ser feita.
//
//Primeiramente as chamdas do algoritmo ao banco para solicitação de 'departamento' estão mais que adequadas e os métodos adequados para carregar o objeto e liberar o fluxo ocupado quando retornado o valor também.
//O que podemos formular para deixar o código mais simples e menos complexo é a criação de uma função que faça a conexão ao banco ser comum nos métodos que esta é requisitada, porém o fechamento do fluxo nesta função 
// poderia interferir no objeto desejado do banco, e a função fecharia a conexão antes mesmo de conseguir pegar o objeto. Caso esta função consiga desempenhar a conexão desejada, ela poderia ser utilizada
// nas três classes do projeto, pois todas desempenham um funcionamento parecido para listagens de conteúdos no banco (Por ID, Listamento de todos e listagem por Campus no caso desta Classe).


// Como um todo, porém o código está bem entendível, desde a parte de conexão, listagem e salvamento de contéudo.
public abstract class ConectarBD {
	private conectBD(){
		Connection conect = null;
		try{
			conect = ConnectionDAO.getInstance().getConnection();
			return conect;
		}finally{
			if((conect != null) && !conect.isClosed())
			conect.close();
		}
	}
}

public abstract class FinalizarConect(){
	private finish(rs, stmt, conn){

		
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		
}

public class DepartmentDAO extends ConectarBD{

	

	public Department findById(int id) throws SQLException{
		//Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			//conn = ConnectionDAO.getInstance().getConnection();
			stmt = conectBD().prepareStatement(
				"SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			public abstract void FinalizarConect();
		}
	}
	
	public List<Department> listAll(boolean onlyActive) throws SQLException{
		// Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			//conn = ConnectionDAO.getInstance().getConnection();
			stmt = conectBD().createStatement();
		
			rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " + 
					(onlyActive ? " WHERE department.active=1" : "") + " ORDER BY department.name");
			
			List<Department> list = new ArrayList<Department>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
		public abstract void FinalizarConect();
		}
	}
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
		// Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			//conn = ConnectionDAO.getInstance().getConnection();
			stmt = conectBD().createStatement();
		
			rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE department.idCampus=" + String.valueOf(idCampus) + (onlyActive ? " AND department.active=1" : "") + " ORDER BY department.name");
			
			List<Department> list = new ArrayList<Department>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			public abstract void FinalizarConect();
		}
	}
	
	public int save(int idUser, Department department) throws SQLException{
		boolean insert = (department.getIdDepartment() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE department SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? WHERE idDepartment=?");
			}
			
			stmt.setInt(1, department.getCampus().getIdCampus());
			stmt.setString(2, department.getName());
			if(department.getLogo() == null){
				stmt.setNull(3, Types.BINARY);
			}else{
				stmt.setBytes(3, department.getLogo());	
			}
			stmt.setInt(4, department.isActive() ? 1 : 0);
			stmt.setString(5, department.getSite());
			stmt.setString(6, department.getFullName());
			stmt.setString(7, department.getInitials());
			
			if(!insert){
				stmt.setInt(8, department.getIdDepartment());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					department.setIdDepartment(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, department);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, department);
			}
			
			return department.getIdDepartment();
		}finally{
			public abstract void FinalizarConect();
		}
	}
	
	private Department loadObject(ResultSet rs) throws SQLException{
		Department department = new Department();
		
		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setInitials(rs.getString("initials"));
		
		return department;
	}
	
}
