/*
 * Copyright 2018 Tallence AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tallence.formeditor.cae;

import com.coremedia.cap.struct.Struct;
import com.tallence.formeditor.cae.annotations.FormElementDefinition;
import com.tallence.formeditor.cae.elements.AbstractFormElement;
import com.tallence.formeditor.cae.elements.FormElement;
import com.tallence.formeditor.cae.factories.GenericFormElementFactory;
import com.tallence.formeditor.cae.parser.AbstractFormElementParser;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AutoWires all parsers from type {@link AbstractFormElementParser} and creates form elements.
 */
@Component
public class FormElementFactory {


  private static final String FORM_DATA_KEY_TYPE = "type";

  private final Map<String, AbstractFormElementParser<?>> typeToParser = new HashMap<>();
  private final Map<String, GenericFormElementFactory> factories;

  public FormElementFactory(List<AbstractFormElementParser> parsers, final ApplicationContext context) {
    parsers.forEach(p -> typeToParser.put(p.getParserKey(), p));

    this.factories = context.getBeansOfType(AbstractFormElement.class).values().stream()
        .map(AbstractFormElement::getClass)
        .collect(Collectors.toMap(
            this::determineKeyForElementClass,
            type -> context.getBean(GenericFormElementFactory.class, type)
        ));
  }


  private String determineKeyForElementClass(Class<? extends FormElement> forClass) {
    final FormElementDefinition annotation = forClass.getAnnotation(FormElementDefinition.class);
    if (annotation != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return forClass.getSimpleName();
  }


  <T extends FormElement> T createFormElement(Struct elementData, String id) {
    return parseType(elementData, id);
  }


  private <T extends FormElement> T parseType(Struct elementData, String id) {
    String type = elementData.getString(FORM_DATA_KEY_TYPE);

    @SuppressWarnings("unchecked")
    AbstractFormElementParser<T> parser = (AbstractFormElementParser<T>) this.typeToParser.get(type);
    if (parser == null) {
      throw new IllegalStateException("Did not find a Parser for type: " + type);
    }

    T formElement = parser.instantiateType(elementData);
    parser.parseBaseFields(formElement, elementData, id);
    parser.parseSpecialFields(formElement, elementData);

    return formElement;
  }

}
