import http from 'k6/http';
import { sleep, check } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';

// k6 Load Test Configuration representing enterprise scaling profiles
export const options = {
  stages: [
    { duration: '5s', target: 100 },  // Ramp-up to 100 concurrent users
    { duration: '10s', target: 500 }, // Scale up to 500 users
    { duration: '10s', target: 1000 }, // Peak load at 1000 concurrent users
    { duration: '5s', target: 0 },    // Ramp-down
  ],
  thresholds: {
    // 95% of requests must complete within 800ms
    http_req_duration: ['p(95)<800'],
    // Request failure rate must be under 2%
    http_req_failed: ['rate<0.02'],
  },
};

export default function () {
  // Requesting the production-like landing endpoint
  const url = 'https://fitnesspaw-2faa2.firebaseapp.com/';
  const res = http.get(url);
  
  check(res, {
    'response code is 200': (r) => r.status === 200,
    'body size is greater than 0': (r) => r.body.length > 0,
  });
  
  sleep(1); // Think time between iterations
}

// Generate the beautiful HTML summary report using k6-reporter
export function handleSummary(data) {
  return {
    'testing/reports/load-test-report.html': htmlReport(data),
  };
}
