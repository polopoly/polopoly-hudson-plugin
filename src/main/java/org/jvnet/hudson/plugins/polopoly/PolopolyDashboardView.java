package org.jvnet.hudson.plugins.polopoly;

import static org.jvnet.hudson.plugins.polopoly.PolopolyConventions.POLOPOLY_CONVENTIONS;
import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewDescriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class PolopolyDashboardView extends View {

    @DataBoundConstructor
    public PolopolyDashboardView(String name) {
        super(name);
    }
    
    /**
     * Test if item matches the Polopoly build name convention.
     */
    @Override
    public boolean contains(TopLevelItem item)
    {
        return POLOPOLY_CONVENTIONS.isPolopolyBuildJob(item);
    }

    /**
     * Called on "New Job"
     */
    @Override
    public Item doCreateItem(StaplerRequest req, StaplerResponse rsp)
        throws IOException, ServletException
    {
        return Hudson.getInstance().doCreateItem(req, rsp);
    }

    /**
     * Return all Hudson jobs matching Polopoly build name convention.
     */
    @Override
    public Collection<TopLevelItem> getItems()
    {
        Collection<TopLevelItem> items = new ArrayList<TopLevelItem>();
        for (TopLevelItem item : Hudson.getInstance().getItems()) {
            if (contains(item)) {
                items.add(item);
            }
        }
        return items;
    }

    public Collection<PolopolyBuild> getPolopolyItems()
    {
        Collection<PolopolyBuild> items = new ArrayList<PolopolyBuild>();
        for (TopLevelItem item : getItems()) {
            items.add(POLOPOLY_CONVENTIONS.createBuildRepresentation(item));
        }
        return items;
    }
    
    @Extension
    public static final class DescriptorImpl extends ViewDescriptor {
        
        public String getDisplayName()
        {
            return "Polopoly Dashboard View";
        }
    }        

    // IGNORED
    public void onJobRenamed(Item arg0, String arg1, String arg2) {}
    protected void submit(StaplerRequest arg0) {}
}
