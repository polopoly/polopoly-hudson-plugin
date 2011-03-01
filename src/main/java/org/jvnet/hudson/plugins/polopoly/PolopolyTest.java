/**
 * 
 */
package org.jvnet.hudson.plugins.polopoly;

import static org.jvnet.hudson.plugins.polopoly.PolopolyConventions.POLOPOLY_CONVENTIONS;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.TopLevelItem;
import hudson.model.Fingerprint.RangeSet;

import java.util.Set;

@SuppressWarnings("unchecked")
public final class PolopolyTest extends PolopolyProject {
    
    private final String runType;
    private final String container;
    private final String database;
    private final String platform;

    private final Set<String> types;

    PolopolyTest(final AbstractProject job,
                 final String version,
                 final String runType,
                 final String container,
                 final String database)
    {
        super(job, version);
        this.runType = runType;
        this.container = container;
        this.database = database;
        this.platform = container + "-" + database;
        this.types = POLOPOLY_CONVENTIONS.createTypesFromRuntype(runType);
    }
    
    public Run getLatest(PolopolyBuild.Run upstreamBuild) {
        RangeSet rs = upstreamBuild.getBuild().getDownstreamRelationship(getProject());
        return rs != null ? get(rs.max()) : null;
    }

    private Run get(int testNumber)
    {
        return new Run((AbstractBuild) getProject().getBuildByNumber(testNumber));
    }

    public Status getStatus()
    {
        return new Status();
    }
    
    public static boolean isPolopolyTest(TopLevelItem job)
    {
        return PolopolyConventions.POLOPOLY_CONVENTIONS.isPolopolyTestJob(job);
    }

    public static PolopolyTest createRepresentation(AbstractProject project)
    {
        return POLOPOLY_CONVENTIONS.createTestRepresentation((TopLevelItem) project);
    }

    // -------------------------------------------------------------------------
    // Bean
    // -------------------------------------------------------------------------
    public String getRunType()
    {
        return runType;
    }
    
    public Set<String> getTypes()
    {
        return types;
    }

    public String getContainer()
    {
        return container;
    }
    
    public String getDatabase()
    {
        return database;
    }
    
    public String getPlatform()
    {
        return platform;
    }
        
    public class Status {
        
        public boolean takesPrecedenceOver(Status that) {
            return that == null
                || that.getResult() == null
                || this.getResult() != null 
                && this.getResult().isWorseThan(that.getResult());
        }
        
        private Result getResult() {
            hudson.model.Run lastBuild = getJob().getLastBuild();
            return lastBuild != null ? lastBuild.getResult() : null;
        }
    }

    public class Run {

        private final AbstractBuild testBuild;

        public Run(AbstractBuild testBuild)
        {
            this.testBuild = testBuild;
        }

        public AbstractBuild getBuild()
        {
            return testBuild;
        }
    }
}