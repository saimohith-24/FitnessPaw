import http from 'k6/http';
import { sleep, check } from 'k6';
import { htmlReport } from './k6-reporter-bundle.js';

// k6 Load Test — CI-safe configuration for FitnessPaw
// Uses local k6-reporter bundle to avoid remote fetch failures in CI
export const options = {
  stages: [
    { duration: '5s',  target: 10  }, // Ramp-up to 10 concurrent users
    { duration: '10s', target: 50  }, // Sustain 50 users
    { duration: '5s',  target: 0   }, // Ramp-down
  ],
  thresholds: {
    // Loosened for CI: 95% of requests under 3s (live Firebase can be slow)
    http_req_duration: ['p(95)<3000'],
    // Allow up to 5% failure rate (network flakiness in CI)
    http_req_failed: ['rate<0.05'],
  },
};

export default function () {
  const url = 'https://fitnesspaw-2faa2.firebaseapp.com/';
  const res = http.get(url, { timeout: '10s' });

  check(res, {
    'response code is 200': (r) => r.status === 200,
    'body size is greater than 0': (r) => r.body && r.body.length > 0,
  });

  sleep(1);
}

// Always write the HTML report — even if thresholds fail
export function handleSummary(data) {
  return {
    'testing/reports/load-test-report.html': htmlReport(data),
  };
}
