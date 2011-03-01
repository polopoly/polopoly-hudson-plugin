package org.jvnet.hudson.plugins.polopoly;

import static org.jvnet.hudson.plugins.polopoly.PolopolyConventions.POLOPOLY_CONVENTIONS;
import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import hudson.util.CaseInsensitiveComparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import javax.servlet.ServletException;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class PolopolyMatrixView extends View {

    private static final String[] TYPES = new String[] { 
        "system", "long", "upgrade", "webapps", "benchmark", "plugins", "deprecated"
    };
    
    @DataBoundConstructor
    public PolopolyMatrixView(String name) {
        super(name);
    }
    
    /**
     * Test if item matches the Polopoly test name convention.
     */
    @Override
    public boolean contains(TopLevelItem item)
    {
        return POLOPOLY_CONVENTIONS.isPolopolyTestJob(item);
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
     * Return all Hudson jobs matching Polopoly test name convention.
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
    
    public Collection<PolopolyTest> getPolopolyItems()
    {
        Collection<PolopolyTest> items = new ArrayList<PolopolyTest>();
        for (TopLevelItem item : getItems()) {
            items.add(POLOPOLY_CONVENTIONS.createTestRepresentation(item));
        }
        return items;
    }
    
    public Collection<String> getVersions(Collection<PolopolyTest> items)
    {
        Collection<String> versions = new TreeSet<String>(new ReverseComparator(CaseInsensitiveComparator.INSTANCE));
        for (PolopolyTest item : items) {
            versions.add(item.getVersion());
        }
        return versions;
    }
    
    public Collection<PolopolyTest> filterByVersion(Collection<PolopolyTest> items, String version)
    {
        Collection<PolopolyTest> itemsUsingVersion = new ArrayList<PolopolyTest>();
        for (PolopolyTest item : items) {
            if (item.getVersion().contains(version)) {
                itemsUsingVersion.add(item);
            }
        }
        return itemsUsingVersion;
    }
    
    public Collection<String> getTypes(Collection<PolopolyTest> items)
    {
        return Arrays.asList(TYPES);
    }

    public Collection<String> getPlatforms(Collection<PolopolyTest> items)
    {
        Collection<String> platforms = new HashSet<String>();
        for (PolopolyTest item : items) {
            platforms.add(item.getPlatform());
        }
        return platforms;
    }

    @Extension
    public static final class DescriptorImpl extends ViewDescriptor {
        
        public String getDisplayName()
        {
            return "Polopoly Matrix View";
        }
    }        

    // IGNORED
    public void onJobRenamed(Item arg0, String arg1, String arg2) {}
    protected void submit(StaplerRequest arg0) {}
}
