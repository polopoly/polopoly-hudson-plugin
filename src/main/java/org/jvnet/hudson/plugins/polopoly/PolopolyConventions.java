package org.jvnet.hudson.plugins.polopoly;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolopolyConventions {
    
    public static final PolopolyConventions POLOPOLY_CONVENTIONS = new PolopolyConventions();

    // Build conventions
    private static final Pattern POLOPOLY_BUILD_NAME_CONVENTION = 
        Pattern.compile("RELENG-(.*?)_Nightly$");
        
    public PolopolyBuild createBuildRepresentation(final TopLevelItem job)
    {
        Matcher matcher = POLOPOLY_BUILD_NAME_CONVENTION.matcher(job.getDisplayName());
        if (!isPolopolyBuildJob(job, matcher)) {
            throw new RuntimeException(job.getDisplayName() + " is no polopoly build job");
        }
        return new PolopolyBuild((AbstractProject) job, getVersion(matcher));
    }

    public boolean isPolopolyBuildJob(final TopLevelItem job)
    {
        return isPolopolyBuildJob(job, POLOPOLY_BUILD_NAME_CONVENTION.matcher(job.getDisplayName()));
    }

    private boolean isPolopolyBuildJob(final TopLevelItem job, final Matcher matcher)
    {
        return (job instanceof AbstractProject) && matcher.matches();        
    }

    // Test conventions
    private static final Pattern POLOPOLY_TEST_NAME_CONVENTION =
        Pattern.compile("RELENG-(.*?)_Nightly_(?:(.*)-)?(.*)-(.*)$");

    public PolopolyTest createTestRepresentation(final TopLevelItem job)
    {
        Matcher matcher = POLOPOLY_TEST_NAME_CONVENTION.matcher(job.getDisplayName());
        if (!isPolopolyTestJob(job, matcher)) {
            throw new IllegalArgumentException(job.getDisplayName() + " is no polopoly test job");
        }
        return new PolopolyTest((AbstractProject) job, getVersion(matcher), getRunType(matcher), getContainer(matcher), getDatabase(matcher));
    }
    
    public boolean isPolopolyTestJob(final TopLevelItem job)
    {
        return isPolopolyTestJob(job, POLOPOLY_TEST_NAME_CONVENTION.matcher(job.getDisplayName()));
    }
    
    public Set<String> createTypesFromRuntype(final String runType)
    {
        Set<String> types = new HashSet<String>(); 
        if (runType.equals("full")) {
            types.add("system");
            types.add("long");
            types.add("webapps");
            types.add("benchmark");
            
        } else if (runType.equals("fullweb")) {
            types.add("benchmark");
            types.add("webapps");
            
        } else if (runType.equals("fulldb")) {
            types.add("system");
            types.add("long");
            
        } else {
            types.add(runType);
        }
        return types;
    }
    
    private boolean isPolopolyTestJob(final TopLevelItem job, final Matcher matcher)
    {
        return (job instanceof AbstractProject) && matcher.matches();        
    }

    // REGEXP EXTRACTORS
    private String getVersion(final Matcher matcher)
    {
        return matcher.group(1);
    }

    private String getRunType(final Matcher matcher)
    {
        String runType = matcher.group(2);
        return (runType != null) ? runType : "system";
    }    
    
    private String getContainer(final Matcher matcher)
    {
        return matcher.group(3);
    }

    private String getDatabase(Matcher matcher)
    {
        return matcher.group(4);
    }
}
