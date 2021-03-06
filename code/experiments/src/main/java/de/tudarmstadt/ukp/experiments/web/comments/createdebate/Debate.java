/*
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
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

package de.tudarmstadt.ukp.experiments.web.comments.createdebate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Habernal
 */
public class Debate
{
    private String title;
    private String description;
    private String url;
    private List<Argument> argumentList = new ArrayList<>();

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<Argument> getArgumentList()
    {
        return argumentList;
    }

    public void setArgumentList(List<Argument> argumentList)
    {
        this.argumentList = argumentList;
    }

    @Override
    public String toString()
    {
        return "Debate{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", argumentList=" + argumentList +
                '}';
    }
}
