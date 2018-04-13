package com.tallence.formeditor.cae.actions;

import com.tallence.formeditor.cae.elements.FormElement;
import com.tallence.formeditor.contentbeans.FormEditor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Wraps the access to the mail system. Used when sending mails to the user or the form admin.
 * Create an implementation by your needs, e.g. sending mails via the elastic social worker or the default java Mail
 */
public interface FormEditorMailAdapter {

  /**
   * Informs the form admin about a new form request.
   *
   * Field values of type {@link com.tallence.formeditor.cae.elements.FileUpload} can not yet be included in the mail.
   *
   * @param target the ContentBean of the current Form Document
   * @param recipient address which will receive the mail.
   * @param formData formData already serialized to one plain string
   * @param elements all the form elements, containing the current form request value.
   * @return true, if the data was saved successfully. False otherwise
   */
  boolean sendAdminMail(FormEditor target, String recipient, String formData, List<FormElement> elements);

  /**
   * Serialize the given data to the te form editor storage.
   *
   * @param target the ContentBean of the current Form Document
   * @param recipient address which will receive the mail.
   * @param formData formData already serialized to one plain string
   * @param elements all the form elements, containing the current form request value.
   * @param files the files, containing all files of the current form request.
   * @return true, if the data was saved successfully. False otherwise
   */
  boolean sendUserMail(FormEditor target, String recipient, String formData, List<FormElement> elements, List<MultipartFile> files);
}
