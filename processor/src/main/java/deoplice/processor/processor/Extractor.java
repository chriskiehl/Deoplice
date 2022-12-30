package deoplice.processor.processor;

import javax.lang.model.element.Element;

/**
 * At the core of the API generation is a set of lenses.
 *
 * This Extractor is about determining if we should extract fields from the
 * supplied Element, and, if so, how we find the getter and setter/wither that
 * we need in order to generate the lens.
 */
public interface Extractor {
    Boolean shouldExtract(Element parent);
    String getter(Element field);
    String setter(Element field);
}
