/**
 * Security Dependency, Secret, and Vulnerability Scan Suite for FitnessPaw
 * Scans code files for secrets and dependencies for vulnerabilities.
 * Generates security-report.html.
 */

const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const startTime = Date.now();
const findings = {
  vulnerabilities: [],
  secrets: [],
  summary: {
    critical: 0,
    high: 0,
    moderate: 0,
    low: 0
  }
};

// 1. Dependency Audit Scan
function scanDependencies() {
  console.log("Auditing Web Application dependencies...");
  const webPkgJson = path.join(__dirname, '../../web/package.json');
  if (!fs.existsSync(webPkgJson)) {
    console.log("web/package.json not found, skipping dependency scan");
    return;
  }

  try {
    const output = execSync('npm audit --json', { cwd: path.join(__dirname, '../../web'), encoding: 'utf8', stdio: ['pipe', 'pipe', 'ignore'] });
    parseNpmAudit(output);
  } catch (err) {
    // npm audit returns non-zero code if vulnerabilities are found
    if (err.stdout) {
      parseNpmAudit(err.stdout);
    } else {
      console.log(`⚠ npm audit execution error: ${err.message}`);
    }
  }
}

function parseNpmAudit(jsonStr) {
  try {
    const data = JSON.parse(jsonStr);
    
    // Check if new format (v7+) or old format
    if (data.vulnerabilities) {
      for (const [pkgName, details] of Object.entries(data.vulnerabilities)) {
        const severity = details.severity || 'moderate';
        findings.summary[severity] = (findings.summary[severity] || 0) + 1;
        findings.vulnerabilities.push({
          packageName: pkgName,
          severity: severity.toUpperCase(),
          effect: details.via?.map(v => typeof v === 'object' ? v.name : v).join(', ') || 'N/A',
          recommendation: `Update ${pkgName} to version ${details.fixAvailable?.name || 'latest'}`,
          title: `Dependency Vulnerability in ${pkgName}`
        });
      }
    } else if (data.advisories) {
      // old format
      for (const advisory of Object.values(data.advisories)) {
        const severity = advisory.severity || 'moderate';
        findings.summary[severity] = (findings.summary[severity] || 0) + 1;
        findings.vulnerabilities.push({
          packageName: advisory.module_name,
          severity: severity.toUpperCase(),
          effect: advisory.overview,
          recommendation: advisory.recommendation,
          title: advisory.title
        });
      }
    }
  } catch (e) {
    console.log(`Failed to parse npm audit JSON: ${e.message}`);
  }
}

// 2. Secret Scan
const SECRET_REGEXES = {
  "GitHub PAT": /ghp_[a-zA-Z0-9]{36}/g,
  "Firebase API Key": /AIzaSy[a-zA-Z0-9\-_]{33}/g,
  "Generic Password": /password\s*=\s*['"][^'"]{8,}['"]/gi,
  "Google Client ID": /[0-9]+-[a-zA-Z0-9_]+\.apps\.googleusercontent\.com/g
};

function scanSecrets(dirPath) {
  const files = getFilesRecursive(dirPath);
  console.log(`Scanning ${files.length} files for exposed credentials/secrets...`);
  
  for (const file of files) {
    // Exclude scanning binary files, node_modules, build directories, and testing reports
    if (
      file.includes('node_modules') || 
      file.includes('build') || 
      file.includes('.git') || 
      file.includes('dist') ||
      file.endsWith('.png') ||
      file.endsWith('.webp') ||
      file.endsWith('.jpg') ||
      file.endsWith('.jar') ||
      file.endsWith('.html') ||
      file.endsWith('.json') ||
      file === __filename // exclude this file
    ) {
      continue;
    }

    try {
      const content = fs.readFileSync(file, 'utf8');
      for (const [secretType, regex] of Object.entries(SECRET_REGEXES)) {
        let match;
        // Reset regex index
        regex.lastIndex = 0;
        while ((match = regex.exec(content)) !== null) {
          // Mask the secret for safety
          const matchedSecret = match[0];
          const masked = matchedSecret.substring(0, 8) + '...' + matchedSecret.substring(matchedSecret.length - 4);
          
          findings.secrets.push({
            file: path.relative(path.join(__dirname, '../..'), file),
            secretType,
            value: masked,
            line: getLineNumber(content, match.index)
          });
        }
      }
    } catch (e) {
      // ignore unreadable files
    }
  }
}

function getFilesRecursive(dir) {
  let results = [];
  const list = fs.readdirSync(dir);
  list.forEach((file) => {
    file = path.join(dir, file);
    const stat = fs.statSync(file);
    if (stat && stat.isDirectory()) {
      results = results.concat(getFilesRecursive(file));
    } else {
      results.push(file);
    }
  });
  return results;
}

function getLineNumber(content, index) {
  const sub = content.substring(0, index);
  return sub.split('\n').length;
}

// Run Main Scan
function main() {
  console.log("=========================================");
  console.log("RUNNING FITNESSPAW CODE SECURITY SCANS");
  console.log("=========================================\n");

  scanDependencies();
  scanSecrets(path.join(__dirname, '../..'));

  generateReport();
}

function generateReport() {
  const duration = Date.now() - startTime;
  const totalIssues = findings.vulnerabilities.length + findings.secrets.length;

  const html = `<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Security Scan Report - FitnessPaw</title>
  <style>
    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background-color: #0b0f19;
      color: #94a3b8;
      margin: 0;
      padding: 40px 20px;
    }
    .container {
      max-width: 900px;
      margin: 0 auto;
    }
    header {
      border-bottom: 2px solid #1e293b;
      padding-bottom: 20px;
      margin-bottom: 30px;
    }
    h1 {
      color: #f8fafc;
      margin: 0 0 10px 0;
      font-size: 32px;
    }
    .subtitle {
      color: #64748b;
      font-size: 16px;
      margin: 0;
    }
    .summary-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
      margin-bottom: 30px;
    }
    .card {
      background-color: #1e293b;
      border: 1px solid #334155;
      border-radius: 8px;
      padding: 20px;
      text-align: center;
    }
    .card-value {
      font-size: 36px;
      font-weight: bold;
      color: #f8fafc;
      margin-bottom: 5px;
    }
    .card-label {
      color: #94a3b8;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 1px;
    }
    .card.critical .card-value { color: #f43f5e; }
    .card.high .card-value { color: #f97316; }
    .card.secrets .card-value { color: #eab308; }
    
    .section-title {
      color: #f8fafc;
      font-size: 20px;
      margin: 30px 0 15px 0;
      border-bottom: 1px solid #1e293b;
      padding-bottom: 8px;
    }
    .issue-table {
      width: 100%;
      border-collapse: collapse;
      background-color: #111827;
      border: 1px solid #1e293b;
      border-radius: 8px;
      overflow: hidden;
      margin-bottom: 30px;
    }
    .issue-table th, .issue-table td {
      padding: 14px 20px;
      text-align: left;
    }
    .issue-table th {
      background-color: #1e293b;
      color: #f1f5f9;
      font-weight: 600;
      font-size: 14px;
    }
    .issue-table tr {
      border-bottom: 1px solid #1e293b;
    }
    .issue-table tr:last-child {
      border-bottom: none;
    }
    .severity-badge {
      display: inline-block;
      padding: 3px 8px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: bold;
    }
    .severity-badge.critical { background-color: rgba(244, 63, 94, 0.15); color: #f43f5e; }
    .severity-badge.high { background-color: rgba(249, 115, 22, 0.15); color: #f97316; }
    .severity-badge.moderate { background-color: rgba(234, 179, 8, 0.15); color: #eab308; }
    .severity-badge.low { background-color: rgba(59, 130, 246, 0.15); color: #3b82f6; }
    
    .recommendation {
      font-size: 13px;
      color: #64748b;
      margin-top: 4px;
    }
  </style>
</head>
<body>
  <div class="container">
    <header>
      <h1>FitnessPaw - Security Scan Report</h1>
      <p class="subtitle">Completed on ${new Date().toLocaleString()} | Dependency Auditor & Credential scanner</p>
    </header>

    <div class="summary-grid">
      <div class="card critical">
        <div class="card-value">${findings.summary.critical || 0}</div>
        <div class="card-label">Critical Issues</div>
      </div>
      <div class="card high">
        <div class="card-value">${findings.summary.high || 0}</div>
        <div class="card-label">High Severity</div>
      </div>
      <div class="card secrets">
        <div class="card-value">${findings.secrets.length}</div>
        <div class="card-label">Secrets Exposed</div>
      </div>
      <div class="card">
        <div class="card-value">${totalIssues}</div>
        <div class="card-label">Total Findings</div>
      </div>
    </div>

    <div class="section-title">Credentials and Secret Exposures</div>
    ${findings.secrets.length === 0 ? '<p style="color: #10b981;">✔ No hardcoded API keys, tokens, or passwords detected in source code files.</p>' : `
      <table class="issue-table">
        <thead>
          <tr>
            <th>Type</th>
            <th>Value Masked</th>
            <th>File Path</th>
            <th>Line</th>
          </tr>
        </thead>
        <tbody>
          ${findings.secrets.map(s => `
            <tr>
              <td><span class="severity-badge critical">${s.secretType}</span></td>
              <td style="font-family: monospace; color: #fda4af;">${s.value}</td>
              <td>${s.file}</td>
              <td>${s.line}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `}

    <div class="section-title">Dependency Vulnerability Advisories</div>
    ${findings.vulnerabilities.length === 0 ? '<p style="color: #10b981;">✔ No npm dependency vulnerabilities reported in package files.</p>' : `
      <table class="issue-table">
        <thead>
          <tr>
            <th>Vulnerability</th>
            <th>Severity</th>
            <th>Package</th>
            <th>Details & Fix</th>
          </tr>
        </thead>
        <tbody>
          ${findings.vulnerabilities.map(v => `
            <tr>
              <td><strong>${v.title}</strong></td>
              <td><span class="severity-badge ${v.severity.toLowerCase()}">${v.severity}</span></td>
              <td>${v.packageName}</td>
              <td>
                <div>Impact: ${v.effect}</div>
                <div class="recommendation">Fix: ${v.recommendation}</div>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `}
  </div>
</body>
</html>`;

  const reportDir = path.join(__dirname, '../reports');
  if (!fs.existsSync(reportDir)) {
    fs.mkdirSync(reportDir, { recursive: true });
  }

  const reportPath = path.join(reportDir, 'security-report.html');
  fs.writeFileSync(reportPath, html);
  console.log(`\n\x1b[36m✔ Security Compliance Report generated at: ${reportPath}\x1b[0m\n`);
}

main();
