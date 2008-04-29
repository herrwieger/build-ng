package org.buildng.reporters;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.stat.Metric;

import org.apache.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;
import org.buildng.commons.hibernate.HibernateUtil;
import org.buildng.elegant.ElegantBuilder;
import org.buildng.flexmetrics.domain.data.Audit;
import org.buildng.flexmetrics.domain.javamm.MetaData;
import org.buildng.flexmetrics.domain.javamm.SourceFile;
import org.buildng.flexmetrics.domain.javamm.SourceFileMgr;
import org.buildng.flexmetrics.domain.version.Version;
import org.buildng.flexmetrics.domain.version.VersionMgr;
import org.buildng.model.Model;
import org.buildng.model.Project;
import org.buildng.model.Reporter;



public class PMDReporter implements Reporter, ReportListener {

    // --------------------------------------------------------------------------
    // class variables
    // --------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(PMDReporter.class);



    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private SourceFile          fCurrentSourceFile;
    private Version             fCurrentVersion;



    // --------------------------------------------------------------------------
    // instance methods
    // --------------------------------------------------------------------------

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void build(Model pModel, Project pProject) {
                      fCurrentVersion = new VersionMgr().getCurrent();
        SourceFileMgr sourceFileMgr   = new SourceFileMgr();
        try {
            PMD pmd = new PMD();
            pmd.setJavaVersion(SourceType.getSourceTypeForId("java 1.5"));
            RuleContext ruleContext = new RuleContext();
            Report      report      = new Report();
            report.addListener(this);

            ruleContext.setReport(report);

            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            ruleSetFactory.setMinimumPriority(Rule.LOWEST_PRIORITY);
            RuleSet     ruleset;
            InputStream rulesInput = new FileInputStream("../buildng/codequality/pmd/pmd.xml");
                        ruleset    = ruleSetFactory.createRuleSet(rulesInput);

            ElegantBuilder   elegant = new ElegantBuilder(pProject.getBaseDir());
            String           javaMainSourceDir = "src/main/java";
            DirectoryScanner scanner           = elegant.fileSet().dir(javaMainSourceDir).includes("**/*.java").get().getDirectoryScanner();
            scanner.scan();
            for (String fileName : scanner.getIncludedFiles()) {
                LOG.debug("checking file " + fileName);
                File file = new File(pProject.getBaseDir(), javaMainSourceDir + File.separator + fileName);
                ruleContext.setSourceCodeFilename(fileName);
                fCurrentSourceFile = sourceFileMgr.findByNameAndVersion(fileName, fCurrentVersion);
                pmd.processFile(new FileInputStream(file), ruleset, ruleContext);
            }
        } catch (Exception ex) {
            LOG.fatal(ex);
            throw new RuntimeException(ex);
        }
    }

    public void metricAdded(Metric pMetric) {
    }

    public void ruleViolationAdded(IRuleViolation pRuleViolation) {
        LOG.debug("violation " + pRuleViolation);
        MetaData metaData = null;
        if (fCurrentSourceFile != null) {
            metaData = fCurrentSourceFile.getBestMatch(pRuleViolation.getClassName(), pRuleViolation.getMethodName(),
                               pRuleViolation.getBeginLine());
        }
        Audit audit = new Audit(metaData, pRuleViolation.getRule().getName(),
                              pRuleViolation.getRule().getPriorityName(), pRuleViolation.getDescription(),
                              fCurrentVersion);
        audit.setLine(pRuleViolation.getBeginLine());
        HibernateUtil.getCurrentSession().save(audit);
    }

    public String equals() {
        try {
        } catch (Exception ex) {
        }
        return "equals";
    }
}
