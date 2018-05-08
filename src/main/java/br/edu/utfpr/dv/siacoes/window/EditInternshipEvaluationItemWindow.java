﻿package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipEvaluationItemBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.model.InternshipEvaluationItem;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditInternshipEvaluationItemWindow extends EditWindow {
	
	private final InternshipEvaluationItem item;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textDescription;
	private final NativeSelect comboType;
	private final TextField textPonderosity;
	private final CheckBox checkActive;
	
	public EditInternshipEvaluationItemWindow(InternshipEvaluationItem item, ListView parentView){
		super("Editar Quesito", parentView);
		
		if(item == null){
			this.item = new InternshipEvaluationItem();
			this.item.setDepartment(Session.getSelectedDepartment().getDepartment());
		}else{
			this.item = item;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textDescription = new TextField("Descrição");
		this.textDescription.setWidth("400px");
		this.textDescription.setMaxLength(255);
		
		this.comboType = new NativeSelect("Avaliação");
		this.comboType.setWidth("150px");
		this.comboType.setNullSelectionAllowed(false);
		this.comboType.addItem(EvaluationItemType.WRITING);
		this.comboType.addItem(EvaluationItemType.ORAL);
		this.comboType.addItem(EvaluationItemType.ARGUMENTATION);
		
		this.textPonderosity = new TextField("Peso");
		this.textPonderosity.setWidth("100px");
		
		this.checkActive = new CheckBox("Ativo");
		
		this.addField(this.comboCampus);
		this.addField(this.comboDepartment);
		this.addField(this.textDescription);
		this.addField(new HorizontalLayout(this.comboType, this.textPonderosity));
		this.addField(this.checkActive);
		
		this.loadEvaluationItem();
		this.textDescription.focus();
	}
	
	private void loadEvaluationItem(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.item.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(this.item.getDepartment());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textDescription.setValue(this.item.getDescription());
		this.textPonderosity.setValue(String.valueOf(this.item.getPonderosity()));
		this.comboType.setValue(this.item.getType());
		this.checkActive.setValue(this.item.isActive());
		
		InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
		
		if(bo.hasScores(this.item.getIdInternshipEvaluationItem())){
			this.textDescription.setEnabled(false);
			this.textPonderosity.setEnabled(false);
			this.comboType.setEnabled(false);
		}
	}
	
	@Override
	public void save() {
		try {
			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
			
			if(!bo.hasScores(this.item.getIdInternshipEvaluationItem())){
				this.item.setDescription(this.textDescription.getValue());
				this.item.setPonderosity(Double.parseDouble(this.textPonderosity.getValue()));
				this.item.setType((EvaluationItemType)this.comboType.getValue());
			}
			
			this.item.setActive(this.checkActive.getValue());
			
			bo.save(this.item);
			
			Notification.show("Salvar Quesito", "Quesito salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Quesito", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
