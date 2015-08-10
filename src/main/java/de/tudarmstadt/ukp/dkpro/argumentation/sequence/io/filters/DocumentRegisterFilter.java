/*
 * Copyright 2015 XXX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tudarmstadt.ukp.dkpro.argumentation.sequence.io.filters;

import de.tudarmstadt.ukp.dkpro.argumentation.sequence.DocumentRegister;
import org.apache.uima.jcas.JCas;

import java.util.HashSet;
import java.util.Set;

/**
 * Filter wrt document register
 *
 * @author xxx
 */
public class DocumentRegisterFilter
        implements DocumentCollectionFilter
{
    private final Set<DocumentRegister> documentRegisters = new HashSet<>();

    public DocumentRegisterFilter(String documentRegistersString)
    {
        // parse document registers
        if (!documentRegistersString.isEmpty()) {
            for (String documentDomainSplit : documentRegistersString.split(" ")) {
                String domain = documentDomainSplit.trim();

                if (!domain.isEmpty()) {
                    documentRegisters.add(DocumentRegister.fromString(domain));
                }
            }
        }
    }

    @Override
    public boolean removeFromCollection(JCas jCas)
    {

        DocumentRegister register = DocumentRegister.fromJCas(jCas);

        return !documentRegisters.isEmpty() && !documentRegisters.contains(register);
    }

    @Override
    public boolean applyFilter()
    {
        return !documentRegisters.isEmpty();
    }

}
