'use strict';

const js = require('@eslint/js');
const nounsanitized = require('eslint-plugin-no-unsanitized');
const security = require('eslint-plugin-security');
const globals = require('globals');

module.exports = [
  js.configs.recommended,
  {
    files: ['assets/js/**/*.js'],
    plugins: { 'no-unsanitized': nounsanitized, security },
    languageOptions: {
      ecmaVersion: 2021,
      sourceType: 'script',
      globals: {
        ...globals.browser,
        // Chirpy theme + Disqus globals provided at runtime.
        Theme: 'readonly',
        DISQUS: 'readonly',
      },
    },
    rules: {
      // Broader Node-style security checks (unsafe regex, eval-with-expression, etc.).
      ...security.configs.recommended.rules,
      // DOM-based XSS guards: flag innerHTML/insertAdjacentHTML/document.write
      // and similar sinks when fed anything other than a constant string.
      'no-unsanitized/property': 'error',
      'no-unsanitized/method': 'error',
      'no-eval': 'error',
      'no-implied-eval': 'error',
      // Empty catch (e) blocks are deliberate "never throw" guards here.
      'no-unused-vars': ['error', { caughtErrors: 'none' }],
    },
  },
  {
    // Lint config itself runs in Node, not the browser.
    files: ['eslint.config.js'],
    languageOptions: {
      sourceType: 'commonjs',
      globals: { ...globals.node },
    },
  },
];
