package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.StructDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TypeAnalyzerIT extends CSharpIntegrationTest{

    @Test
    void testStruct(){
        List<StructDescriptor> structs = query("Match (s:Struct) return s").getColumn("s");

        assertThat(structs.size()).isEqualTo(2);
    }

}
