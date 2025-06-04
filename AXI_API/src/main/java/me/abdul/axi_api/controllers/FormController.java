package me.abdul.axi_api.controllers;

import me.abdul.axi_api.dtos.FormDto;
import me.abdul.axi_api.entities.Form;
import me.abdul.axi_api.entities.Team;
import me.abdul.axi_api.repos.TeamRepository;
import me.abdul.axi_api.services.FormService;
import me.abdul.axi_api.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/team")
@RestController
public class FormController {
    final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;

    }

    @GetMapping("/{id}/forms")
    public ResponseEntity<List<Form>> getForms(@PathVariable Integer id) {
        List<Form> forms = formService.getFormsByTeamId(id);
        return ResponseEntity.ok(forms);
    }

    @GetMapping("/{id}/active/forms")
    public ResponseEntity<List<Form>> getActiveForms(@PathVariable Integer id) {
        List<Form> forms = formService.getFormsByTeamIdAndActive(id);
        return ResponseEntity.ok(forms);
    }

    @GetMapping("/{id}/form/{formId}")
    public ResponseEntity<Form> getForm(@PathVariable Integer id, @PathVariable Integer formId) {
        Form form = formService.getFormById(formId);
        return ResponseEntity.ok(form);
    }

    @PutMapping("/{id}/form/{formId}/status")
    public ResponseEntity<Form> formStatus(@PathVariable Integer id, @PathVariable Integer formId, @RequestBody Boolean isActive) {
        Form form = formService.updateFormStatus(formId, isActive);
        return ResponseEntity.ok(form);
    }

    @PostMapping("/{id}/form/create")
    public ResponseEntity<Form> createForm(@PathVariable Integer id, @RequestBody FormDto form) {
        Form createdForm = formService.createForm(form, id);
        return ResponseEntity.ok(createdForm);
    }

    @PutMapping("/{id}/form/{formId}/update")
    public ResponseEntity<Form> updateForm(@PathVariable Integer id, @PathVariable Integer formId, @RequestBody FormDto form) {
        Form updatedForm = formService.updateForm(formId, form);
        return ResponseEntity.ok(updatedForm);
    }

    @GetMapping("/forms")
    public ResponseEntity<List<Form>> getAllForms() {
        List<Form> forms = formService.getAllForms();
        return ResponseEntity.ok(forms);
    }

    @PostMapping("{id}/form/{formId}/add")
    public ResponseEntity<Form> addFormToTeam(@PathVariable Integer id, @PathVariable Integer formId) {
        Form form = formService.addFormToTeam(id, formId);
        return ResponseEntity.ok(form);
    }

    @DeleteMapping("{id}/form/{formId}/remove")
    public ResponseEntity<Form> removeFormFromTeam(@PathVariable Integer id, @PathVariable Integer formId) {
        Form form = formService.removeFormFromTeam(id, formId);
        return ResponseEntity.ok(form);
    }

    @GetMapping("/forms/{id}")
    public ResponseEntity<List<Form>> getFormsByCategoryId(@PathVariable Integer id) {
        List<Form> forms = formService.getFormsByCategoryId(id);
        return ResponseEntity.ok(forms);
    }





}
