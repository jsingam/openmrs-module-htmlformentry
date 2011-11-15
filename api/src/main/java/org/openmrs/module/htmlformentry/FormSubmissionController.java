package org.openmrs.module.htmlformentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.action.RepeatControllerAction;

/**
 * Encapsulates how to validate and submit a form.
 * <p/>
 * When going through XML/HTML substitution to build a form, one of these is created as a side-effect.
 */
public class FormSubmissionController {
    
    private List<FormSubmissionControllerAction> actions = new ArrayList<FormSubmissionControllerAction>();
    private transient List<FormSubmissionError> lastSubmissionErrors;
    private transient HttpServletRequest lastSubmission;
    private RepeatControllerAction repeat = null;
    
    public FormSubmissionController() {
    }
    
    /**
     * Adds a {@see RepeatControllerAction} to the list of submission actions.
     * 
     * @param repeat the repeat controller action to add
     */
    public void startRepeat(RepeatControllerAction repeat) {
        if (this.repeat != null)
            throw new IllegalArgumentException("Nested Repeating elements are not yet implemented");
        addAction(repeat);
        this.repeat = repeat;
    }
    
    /**
     * Marks the end of the a repeat. This has to be specified because nested repeating elements are not yet implemented.
     */
    public void endRepeat() {
        if (this.repeat == null)
            throw new IllegalArgumentException("No Repeating element is open now");
        this.repeat = null;
    }
    
    /**
     * Adds a FormSubmissionControllerAction to the list of submission actions.
     * 
     * @param the form submission controller action to add
     */
    public void addAction(FormSubmissionControllerAction action) {
        actions.add(action);
    }
    
    /**
     * Validates a form submission, given a Form Entry Context.
     * <p/>
     * This method cycles through all the FormSubmissionControllerActions and calls their validateSubmission method, 
     * adding any errors to the error list.
     * 
     * @param context the Form Entry Context 
     * @param submission the submission to validate
     * @return list of all validation errors
     */
    public List<FormSubmissionError> validateSubmission(FormEntryContext context, HttpServletRequest submission) {
        lastSubmission = submission;
        lastSubmissionErrors = new ArrayList<FormSubmissionError>();
        for (FormSubmissionControllerAction element : actions) {
            Collection<FormSubmissionError> errs = element.validateSubmission(context, submission);
            if (errs != null)
                lastSubmissionErrors.addAll(errs);
        }
        return lastSubmissionErrors;
    }
    
    /**
     * Processes a form submission, given a Form Entry Session.
     * <p/>
     * This method cycles through all the FormSubmissionControllerActions and calls their handleSubmission method,
     * 
     * @param session the Form Entry Session
     * @param submission
     */
    public void handleFormSubmission(FormEntrySession session, HttpServletRequest submission) {
        lastSubmission = submission;
        for (FormSubmissionControllerAction element : actions) {
            element.handleSubmission(session, submission);
        }
    }
    
    /**
     * Returns the last submission processed by handleFormSubmission.
     * 
     * @return the last submission processed
     */
    public HttpServletRequest getLastSubmission() {
        return lastSubmission;
    }

    /**
     * Returns the last set of submission errors generated by validateSubmission
     * 
     * @return the last set of submission errors
     */
    public List<FormSubmissionError> getLastSubmissionErrors() {
        return lastSubmissionErrors;
    }
    
    /**
     * 
     * Returns the List of FormSubmissionControllerActions
     * 
     * @return the FormSubmissionControllerAction List
     */
    public List<FormSubmissionControllerAction> getActions() {
               return actions;
    }
    
}
