package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Defines label "CSharp" which is applied to all nodes generated by this plugin.
 */
@Abstract
@Label("CSharp")
public interface CSharpDescriptor extends Descriptor {}
