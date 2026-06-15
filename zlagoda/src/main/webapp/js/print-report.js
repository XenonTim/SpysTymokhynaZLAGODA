
function openPrintPreview(reportTitle, tableId) {
    var table = document.getElementById(tableId);
    if (!table) { alert('Table not found: ' + tableId); return; }

    var tableHTML = table.outerHTML;
    var now = new Date();
    var dateStr = now.toLocaleDateString('en-GB') + '  ' + now.toLocaleTimeString('en-GB');

    var html = '<!DOCTYPE html><html lang="en"><head>' +
        '<meta charset="UTF-8">' +
        '<title>' + reportTitle + '</title>' +
        '<style>' +
        '  @page { margin: 20mm 15mm; }' +
        '  * { box-sizing: border-box; margin: 0; padding: 0; }' +
        '  body { font-family: Arial, sans-serif; font-size: 12px; color: #000; }' +

        '  #report-header { text-align: center; margin-bottom: 12px; border-bottom: 2px solid #000; padding-bottom: 8px; }' +
        '  #report-header h1 { font-size: 16px; font-weight: bold; }' +
        '  #report-header p  { font-size: 11px; color: #555; margin-top: 4px; }' +
        '  #report-footer { text-align: center; margin-top: 16px; border-top: 1px solid #aaa; padding-top: 6px; font-size: 10px; color: #555; }' +

        '  table { width: 100%; border-collapse: collapse; margin-top: 8px; }' +
        '  thead tr { background-color: #333; color: #fff; }' +
        '  th, td { border: 1px solid #ccc; padding: 5px 8px; text-align: left; vertical-align: middle; }' +
        '  tbody tr:nth-child(even) { background-color: #f5f5f5; }' +
        '  .no-print { display: none !important; }' +
        '</style>' +
        '</head><body>' +

        '<div id="report-header">' +
        '  <h1>ZLAGODA — ' + reportTitle + '</h1>' +
        '  <p>Generated: ' + dateStr + '</p>' +
        '</div>' +

        tableHTML +

        '<div id="report-footer">' +
        '  &copy; ' + now.getFullYear() + ' ZLAGODA Supermarket &nbsp;|&nbsp; ' +
        '  Report generated automatically by ZLAGODA AIS' +
        '</div>' +

        '</body></html>';

    var win = window.open('', '_blank', 'width=900,height=700');
    win.document.write(html);
    win.document.close();
    win.focus();
    win.print();
}
