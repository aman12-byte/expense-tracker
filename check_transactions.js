const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  page.on('console', msg => console.log('PAGE LOG:', msg.text()));
  page.on('pageerror', error => console.log('PAGE ERROR:', error.message));
  page.on('response', response => console.log('RESPONSE:', response.url(), response.status()));
  
  await page.goto('http://localhost:8080/transactions.html');
  await page.waitForTimeout(2000);
  
  const html = await page.evaluate(() => {
     return document.getElementById('tx-tbody').innerHTML;
  });
  console.log("TBODY HTML:", html);
  
  await browser.close();
})();
