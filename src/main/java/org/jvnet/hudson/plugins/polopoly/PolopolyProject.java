package org.jvnet.hudson.plugins.polopoly;

import hudson.model.AbstractProject;
import hudson.model.Job;

@SuppressWarnings("unchecked")
public class PolopolyProject {
    
    private AbstractProject job;
    private String version;

    PolopolyProject(AbstractProject job, String version)
    {
        this.job = job;
        this.version = version;
    }
    
    public AbstractProject getProject()
    {
        return job;
    }
    
    public Job getJob()
    {
        return getProject();
    }

    public String getVersion()
    {
        return version;
    }
}
