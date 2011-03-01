package org.jvnet.hudson.plugins.polopoly;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class PolopolyBuild extends PolopolyProject {
    
    PolopolyBuild(final AbstractProject job, final String version)
    {
        super(job, version);
    }
        
    public Run get(int buildNo)
    {
        return new Run((AbstractBuild) getProject().getBuildByNumber(buildNo));
    }

    public Run getLatest() {
        return get(getProject().getLastBuild().number);
    }
    
    private List<PolopolyTest> getDownstreamTests() {
        List<PolopolyTest> polopolyTests = new ArrayList<PolopolyTest>();
        List<AbstractProject> downstreamProjects = getProject().getDownstreamProjects();
        for (AbstractProject project : downstreamProjects) {
            try {
                polopolyTests.add(PolopolyTest.createRepresentation(project));
            } catch (IllegalArgumentException ignoreNonPolooolyTest) { }
        }
        return polopolyTests;
    }

    public class Run {
        
        private final AbstractBuild build;

        private Run(AbstractBuild build)
        {
            this.build = build;
        }
        
        public AbstractBuild getBuild() {
            return build;
        }
     
        public List<PolopolyTest.Run> getLatestTestResult() {
            List<PolopolyTest.Run> testResults = new ArrayList<PolopolyTest.Run>();
            for (PolopolyTest testProject : getDownstreamTests()) {
                PolopolyTest.Run latestTestResult = testProject.getLatest(this);
                if (latestTestResult != null) {
                    testResults.add(latestTestResult);
                }
            }
            return testResults;
        }
        
        private void hej() {
            getLatestTestResult().get(0).getBuild();
        }
    }
    
    public static boolean isPolopolyBuild(TopLevelItem item) {
        return PolopolyConventions.POLOPOLY_CONVENTIONS.isPolopolyBuildJob(item);
    }
        
}