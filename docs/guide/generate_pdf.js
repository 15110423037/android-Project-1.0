/**
 * Threads Clone Android — Documentation to HTML Generator
 * 
 * This script reads all markdown documentation files and generates
 * a single, professionally styled HTML file that can be printed to PDF.
 * 
 * Usage: node generate_pdf.js
 * Then open the generated HTML in a browser and press Ctrl+P → Save as PDF
 */

const fs = require('fs');
const path = require('path');

// Read local mermaid.js (downloaded via: Invoke-WebRequest https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js)
const mermaidJsPath = path.join(__dirname, 'mermaid.min.js');
const mermaidJs = fs.existsSync(mermaidJsPath) ? fs.readFileSync(mermaidJsPath, 'utf8') : null;
if (!mermaidJs) { console.warn('Warning: mermaid.min.js not found — diagrams won\'t render'); }

const chapters = [
  '01_PROJECT_OVERVIEW.md',
  '02_SETUP_GUIDE.md',
  '03_FILE_STRUCTURE.md',
  '04_DEPENDENCIES.md',
  '05_DATA_MODELS.md',
  '06_BASE_CLASSES.md',
  '07_ACTIVITIES.md',
  '08_FRAGMENTS.md',
  '09_UTILS_SERVICES_DB.md',
  '10_FIREBASE.md',
  '11_UI_LAYOUTS.md',
  '12_VIVA_QA.md',
];

// Simple markdown to HTML converter (no external deps needed)
function mdToHtml(md) {
  // Normalize Windows CRLF to LF so all regexes work uniformly
  let html = md.replace(/\r\n/g, '\n').replace(/\r/g, '\n');

  // Protect code blocks first (including mermaid)
  const codeBlocks = [];
  html = html.replace(/^```([\w]*)\n([\s\S]*?)^```/gm, (match, lang, code) => {
    const idx = codeBlocks.length;
    const escaped = code.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    if (lang === 'mermaid') {
      codeBlocks.push(`<div class="mermaid">${code.trim()}</div>`);
    } else {
      codeBlocks.push(`<pre><code class="language-${lang || 'text'}">${escaped}</code></pre>`);
    }
    return `%%CODEBLOCK_${idx}%%`;
  });

  // Inline code
  html = html.replace(/`([^`]+)`/g, '<code>$1</code>');

  // Headers
  html = html.replace(/^#### (.+)$/gm, '<h4>$1</h4>');
  html = html.replace(/^### (.+)$/gm, '<h3>$1</h3>');
  html = html.replace(/^## (.+)$/gm, '<h2>$1</h2>');
  html = html.replace(/^# (.+)$/gm, '<h1>$1</h1>');

  // Horizontal rules
  html = html.replace(/^---$/gm, '<hr>');

  // Bold and italic
  html = html.replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>');
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
  html = html.replace(/\*(.+?)\*/g, '<em>$1</em>');

  // Blockquotes
  html = html.replace(/^> (.+)$/gm, '<blockquote>$1</blockquote>');

  // Tables
  html = html.replace(/^\|(.+)\|$/gm, (match, content) => {
    const cells = content.split('|').map(c => c.trim());
    // Check if it's a separator row
    if (cells.every(c => /^[-:]+$/.test(c))) {
      return '%%TABLE_SEP%%';
    }
    const cellHtml = cells.map(c => `<td>${c}</td>`).join('');
    return `<tr>${cellHtml}</tr>`;
  });

  // Wrap consecutive tr elements in table
  html = html.replace(/((?:<tr>.*<\/tr>\n?)+)/g, (match) => {
    let tableContent = match.replace(/%%TABLE_SEP%%\n?/g, '');
    // Make first row header
    tableContent = tableContent.replace(/<tr>(.*?)<\/tr>/, (m, inner) => {
      const headerRow = inner.replace(/<td>/g, '<th>').replace(/<\/td>/g, '</th>');
      return `<thead><tr>${headerRow}</tr></thead><tbody>`;
    });
    return `<table>${tableContent}</tbody></table>`;
  });
  html = html.replace(/%%TABLE_SEP%%/g, '');

  // Unordered lists
  html = html.replace(/^- (.+)$/gm, '<li>$1</li>');
  html = html.replace(/((?:<li>.*<\/li>\n?)+)/g, '<ul>$1</ul>');

  // Ordered lists
  html = html.replace(/^\d+\. (.+)$/gm, '<li>$1</li>');

  // Links
  html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2">$1</a>');

  // Paragraphs (lines that aren't already wrapped in HTML)
  html = html.replace(/^(?!<[a-zA-Z%]|$)(.+)$/gm, '<p>$1</p>');

  // Restore code blocks
  codeBlocks.forEach((block, idx) => {
    html = html.replace(`%%CODEBLOCK_${idx}%%`, block);
  });

  // Clean up empty paragraphs
  html = html.replace(/<p>\s*<\/p>/g, '');
  html = html.replace(/<p><div/g, '<div');
  html = html.replace(/<\/div><\/p>/g, '</div>');
  html = html.replace(/<p><hr><\/p>/g, '<hr>');
  html = html.replace(/<p><h/g, '<h');
  html = html.replace(/<\/h(\d)><\/p>/g, '</h$1>');
  html = html.replace(/<p><table>/g, '<table>');
  html = html.replace(/<\/table><\/p>/g, '</table>');
  html = html.replace(/<p><ul>/g, '<ul>');
  html = html.replace(/<\/ul><\/p>/g, '</ul>');
  html = html.replace(/<p><blockquote>/g, '<blockquote>');
  html = html.replace(/<\/blockquote><\/p>/g, '</blockquote>');
  html = html.replace(/<p><pre>/g, '<pre>');
  html = html.replace(/<\/pre><\/p>/g, '</pre>');

  return html;
}

// Read all chapter files
const guideDir = __dirname;
let allContent = '';

chapters.forEach((file, idx) => {
  const filePath = path.join(guideDir, file);
  if (fs.existsSync(filePath)) {
    const content = fs.readFileSync(filePath, 'utf8');
    allContent += content + '\n\n';
    if (idx < chapters.length - 1) {
      allContent += '<div class="page-break"></div>\n\n';
    }
  }
});

const bodyHtml = mdToHtml(allContent);

const fullHtml = `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Threads Clone Android — Complete Documentation Guide</title>
<style>
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=JetBrains+Mono:wght@400;500&display=swap');

  :root {
    --primary: #1a1a2e;
    --secondary: #16213e;
    --accent: #0f3460;
    --highlight: #e94560;
    --bg: #ffffff;
    --text: #1a1a2e;
    --text-light: #555;
    --border: #e0e0e0;
    --code-bg: #f6f8fa;
    --table-header: #f0f4f8;
    --blockquote-border: #e94560;
    --blockquote-bg: #fff5f5;
  }

  * { margin: 0; padding: 0; box-sizing: border-box; }

  body {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
    line-height: 1.7;
    color: var(--text);
    background: var(--bg);
    font-size: 11pt;
    padding: 0;
  }

  /* COVER PAGE */
  .cover-page {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 40%, #0f3460 100%);
    color: white;
    text-align: center;
    padding: 3rem;
    page-break-after: always;
  }

  .cover-page h1 {
    font-size: 2.8rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
    letter-spacing: -1px;
    border: none;
    color: white;
    padding: 0;
  }

  .cover-page .subtitle {
    font-size: 1.2rem;
    font-weight: 300;
    color: rgba(255,255,255,0.8);
    max-width: 600px;
    margin-bottom: 2rem;
  }

  .cover-page .meta {
    font-size: 0.9rem;
    color: rgba(255,255,255,0.6);
  }

  .cover-page .badge {
    display: inline-block;
    background: var(--highlight);
    color: white;
    padding: 0.4rem 1.2rem;
    border-radius: 99px;
    font-size: 0.85rem;
    font-weight: 600;
    margin-top: 1rem;
  }

  /* TOC PAGE */
  .toc-page {
    page-break-after: always;
    padding: 3rem;
  }

  .toc-page h2 {
    font-size: 1.6rem;
    border-bottom: 3px solid var(--highlight);
    padding-bottom: 0.5rem;
    margin-bottom: 1.5rem;
  }

  .toc-list { list-style: none; padding: 0; }
  .toc-list li {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    padding: 0.6rem 0;
    border-bottom: 1px dashed var(--border);
    font-size: 1rem;
  }

  .toc-list .ch-num {
    display: inline-block;
    width: 2.5rem;
    font-weight: 700;
    color: var(--highlight);
  }

  /* CONTENT */
  .content {
    padding: 2rem 3rem;
    max-width: 100%;
  }

  h1 {
    font-size: 1.8rem;
    font-weight: 700;
    color: var(--primary);
    border-bottom: 3px solid var(--highlight);
    padding-bottom: 0.5rem;
    margin: 2rem 0 1rem;
  }

  h2 {
    font-size: 1.35rem;
    font-weight: 600;
    color: var(--secondary);
    border-bottom: 2px solid var(--border);
    padding-bottom: 0.4rem;
    margin: 1.8rem 0 0.8rem;
  }

  h3 {
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--accent);
    margin: 1.4rem 0 0.6rem;
  }

  h4 {
    font-size: 1rem;
    font-weight: 600;
    color: var(--text);
    margin: 1rem 0 0.4rem;
  }

  p {
    margin: 0.5rem 0;
    color: var(--text);
  }

  a { color: var(--accent); text-decoration: none; }
  a:hover { text-decoration: underline; }

  strong { font-weight: 600; }

  /* CODE */
  code {
    font-family: 'JetBrains Mono', 'Consolas', 'Courier New', monospace;
    background: var(--code-bg);
    padding: 0.15rem 0.35rem;
    border-radius: 4px;
    font-size: 0.85em;
    color: #c7254e;
  }

  pre {
    background: #1e1e2e;
    color: #cdd6f4;
    border-radius: 8px;
    padding: 1rem 1.2rem;
    overflow-x: auto;
    margin: 0.8rem 0;
    font-size: 0.82em;
    line-height: 1.5;
    border: 1px solid #313244;
  }

  pre code {
    background: none;
    color: inherit;
    padding: 0;
    font-size: 1em;
  }

  /* TABLES */
  table {
    width: 100%;
    border-collapse: collapse;
    margin: 0.8rem 0;
    font-size: 0.9em;
  }

  th {
    background: var(--table-header);
    font-weight: 600;
    text-align: left;
    padding: 0.6rem 0.8rem;
    border: 1px solid var(--border);
    color: var(--primary);
  }

  td {
    padding: 0.5rem 0.8rem;
    border: 1px solid var(--border);
    vertical-align: top;
  }

  tr:nth-child(even) { background: #fafafa; }

  /* BLOCKQUOTES */
  blockquote {
    border-left: 4px solid var(--blockquote-border);
    background: var(--blockquote-bg);
    padding: 0.8rem 1.2rem;
    margin: 0.8rem 0;
    border-radius: 0 6px 6px 0;
    color: var(--text-light);
    font-style: italic;
  }

  /* LISTS */
  ul, ol {
    margin: 0.5rem 0;
    padding-left: 1.5rem;
  }

  li {
    margin: 0.3rem 0;
    line-height: 1.6;
  }

  /* HORIZONTAL RULE */
  hr {
    border: none;
    border-top: 2px solid var(--border);
    margin: 1.5rem 0;
  }

  /* MERMAID DIAGRAMS */
  .mermaid {
    background: #f8f9fa;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    padding: 1.5rem 1rem;
    margin: 1rem 0;
    text-align: center;
    overflow-x: auto;
  }

  .mermaid svg {
    max-width: 100%;
    height: auto;
  }

  /* PAGE BREAKS */
  .page-break { page-break-after: always; }

  /* PRINT STYLES */
  @media print {
    body { font-size: 10pt; }
    .cover-page {
      min-height: 100vh;
      background: #1a1a2e !important;
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
    }
    .content { padding: 1rem 2rem; }
    pre { font-size: 0.75em; }
    h1 { page-break-before: auto; }
    table, pre, blockquote { page-break-inside: avoid; }
    .page-break { page-break-after: always; }
    a { color: var(--accent); }
    a[href]::after { content: none; }
  }

  /* FOOTER */
  .footer {
    text-align: center;
    padding: 2rem;
    color: var(--text-light);
    font-size: 0.85rem;
    border-top: 1px solid var(--border);
    margin-top: 2rem;
  }
</style>
</head>
<body>

<!-- COVER PAGE -->
<div class="cover-page">
  <h1>📱 Threads Clone Android</h1>
  <p class="subtitle">Complete Documentation Guide — A comprehensive guide covering architecture, setup, code analysis, Firebase integration, and viva preparation</p>
  <p class="meta">March 2026 · Project Documentation</p>
  <span class="badge">12 Chapters · 52 Viva Questions · 20+ Diagrams</span>
</div>

<!-- TABLE OF CONTENTS -->
<div class="toc-page">
  <h2>📑 Table of Contents</h2>
  <ul class="toc-list">
    <li><span class="ch-num">01</span> Project Overview & Architecture</li>
    <li><span class="ch-num">02</span> Project Setup Guide</li>
    <li><span class="ch-num">03</span> Project File Structure</li>
    <li><span class="ch-num">04</span> Dependencies Explained</li>
    <li><span class="ch-num">05</span> Data Models</li>
    <li><span class="ch-num">06</span> Base Classes (BaseActivity & BaseApplication)</li>
    <li><span class="ch-num">07</span> Activities Deep Dive</li>
    <li><span class="ch-num">08</span> Fragments Deep Dive</li>
    <li><span class="ch-num">09</span> Utilities, Services & Database</li>
    <li><span class="ch-num">10</span> Firebase Integration</li>
    <li><span class="ch-num">11</span> UI & Layouts</li>
    <li><span class="ch-num">12</span> Viva Q&A Reference (52 Questions)</li>
  </ul>
</div>

<!-- MAIN CONTENT -->
<div class="content">
${bodyHtml}
</div>

<!-- FOOTER -->
<div class="footer">
  <p><strong>Threads Clone Android — Complete Documentation</strong></p>
  <p>Generated on ${new Date().toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}</p>
</div>

<!-- Mermaid.js embedded inline -- works with file:// protocol -->
${mermaidJs ? `<script>${mermaidJs}</script>` : ''}
<script>
  if (typeof mermaid !== 'undefined') {
    mermaid.initialize({
      startOnLoad: true,
      theme: 'default',
      securityLevel: 'loose',
      flowchart: { useMaxWidth: true, htmlLabels: true, curve: 'basis' },
      sequence: { useMaxWidth: true },
      classDiagram: { useMaxWidth: true },
      themeVariables: {
        primaryColor: '#e3f2fd',
        primaryTextColor: '#1a1a2e',
        primaryBorderColor: '#0f3460',
        lineColor: '#555',
        secondaryColor: '#fff3e0',
        tertiaryColor: '#f3e5f5'
      }
    });
  }
</script>

</body>
</html>`;

// Write the HTML file
const outputPath = path.join(guideDir, 'Threads_Clone_Documentation.html');
fs.writeFileSync(outputPath, fullHtml, 'utf8');

console.log('✅ HTML documentation generated successfully!');
console.log('📄 Output: ' + outputPath);
console.log('');
console.log('To create a PDF:');
console.log('  1. Open the HTML file in your browser (Chrome recommended)');
console.log('  2. Press Ctrl+P (or Cmd+P on Mac)');
console.log('  3. Set Destination to "Save as PDF"');
console.log('  4. Enable "Background graphics" in More Settings');
console.log('  5. Click Save');
