package me.abdul.axi_api.services;

import me.abdul.axi_api.dtos.FormDto;
import me.abdul.axi_api.entities.Form;
import me.abdul.axi_api.entities.FormCategory;
import me.abdul.axi_api.entities.Question;
import me.abdul.axi_api.entities.Team;
import me.abdul.axi_api.exceptions.AccessDeniedException;
import me.abdul.axi_api.exceptions.NotFoundException;
import me.abdul.axi_api.exceptions.TeamHasNoFormsException;
import me.abdul.axi_api.exceptions.TeamNotFoundException;
import me.abdul.axi_api.repos.FormCategoryRepository;
import me.abdul.axi_api.repos.FormRepository;
import me.abdul.axi_api.repos.QuestionRepository;
import me.abdul.axi_api.repos.TeamRepository;
import org.hibernate.annotations.NotFound;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FormService {

    private final FormRepository formRepository;
    private final TeamRepository teamRepository;
    private final QuestionRepository questionRepository;
    private final FormCategoryRepository formCategoryRepository;

    public FormService(FormRepository formRepository, TeamRepository teamRepository, FormCategoryRepository formCategoryRepository, QuestionRepository questionRepository) {
        this.formRepository = formRepository;
        this.teamRepository = teamRepository;
        this.formCategoryRepository = formCategoryRepository;
        this.questionRepository = questionRepository;
    }

    public Form createForm(FormDto form, int teamId) {
        FormCategory category = formCategoryRepository.findById(form.getCategoryId()).orElseThrow(() -> new NotFoundException("No category found with this id: " + form.getCategoryId()));
        List<Question> questions = questionRepository.findAllById(form.getQuestionIds());

        Form newForm = new Form();
        newForm.setName(form.getName());
        newForm.setIsActive(form.getIsActive());
        newForm.setIsPublic(form.getIsPublic());
        newForm.setCategory(category);
        newForm.setTeamIds(List.of(teamId));
        newForm.setQuestions(questions);

        return formRepository.save(newForm);
    }

    public Form getFormById(int id) {
        return formRepository.findById(id).orElse(null);
    }

    public List<Form> getFormsByTeamId(int teamId) {
       return formRepository.findByTeamIds(teamId).orElseThrow( () -> new TeamHasNoFormsException("No forms found for team with id: " + teamId));
    }

    public List<Form> getFormsByTeamIdAndActive(int teamId) {
        return formRepository.findAllByIsActiveAndTeamIds( true, teamId).orElseThrow( () -> new TeamHasNoFormsException("No forms found for team with id: " + teamId));
    }

    public Form updateFormStatus(int formId, boolean isActive) {
        Form form = formRepository.findById(formId).orElseThrow( () -> new TeamHasNoFormsException("No form found with this id: " + formId));
        form.setIsActive(isActive);
        return formRepository.save(form);

    }

    public Form updateForm(int formId, FormDto form) {
        Form existingForm = formRepository.findById(formId).orElseThrow(() -> new NotFoundException("No form found with this id: " + formId));

        if(existingForm.getIsPublic()) {
            throw new AccessDeniedException("Form is public and cannot be updated");
        }

        FormCategory category = formCategoryRepository.findById(form.getCategoryId()).orElseThrow(() -> new NotFoundException("No category found with this id: " + form.getCategoryId()));
        List<Question> questions = questionRepository.findAllById(form.getQuestionIds());

        existingForm.setName(form.getName());
        existingForm.setIsActive(form.getIsActive());
        existingForm.setCategory(category);
        existingForm.setQuestions(questions);
        existingForm.setIsPublic(form.getIsPublic());


        return formRepository.saveAndFlush(existingForm);
    }

    public List<Form> getAllForms() {
        return formRepository.findAllByIsPublic(true).orElseThrow(() -> new NotFoundException("No forms found"));
    }

    public Form addFormToTeam(int teamId, int formId) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new NotFoundException("No form found with this id: " + formId));

        List<Integer> teamIds = form.getTeamIds();
        teamIds.add(teamId);
        form.setTeamIds(teamIds);

        return formRepository.saveAndFlush(form);
    }

    public Form removeFormFromTeam(int teamId, int formId) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new NotFoundException("No form found with this id: " + formId));

        List<Integer> teamIds = form.getTeamIds();
        if (teamIds.contains(teamId)) {
            teamIds.remove(Integer.valueOf(teamId));
        } else {
            throw new NotFoundException("Team ID " + teamId + " is not associated with this form.");
        }
        form.setTeamIds(teamIds);

        return formRepository.saveAndFlush(form);
    }

    public List<Form> getFormsByCategoryId(int id) {
        FormCategory category = formCategoryRepository.findById(id).orElseThrow(() -> new NotFoundException("No category found with this id: " + id));
        return formRepository.findAllByCategory(category).orElseThrow(() -> new NotFoundException("No forms found with this category"));
    }

}
