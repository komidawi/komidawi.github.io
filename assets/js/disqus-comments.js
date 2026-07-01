/*
 * Collapsible Disqus comments.
 * - The thread is hidden until the reader clicks the toggle (lazy-loads embed.js).
 * - The toggle shows the comment count: "Show comments (7)" / "No comments".
 * - The button is always expandable, even with zero comments.
 *
 * Resilience: everything runs inside an IIFE (no globals leak) and every external
 * interaction with Disqus is guarded. If an API endpoint moves, fails to load, or
 * returns an unexpected payload, the UI degrades to a neutral "Show comments" that
 * still opens the thread. There are no loops, polling, or observers, so nothing can
 * freeze or spin, and all styles are scoped to .comments-* so the page is untouched.
 *
 * Liquid-injected values (shortname, page url, identifier) are read from the
 * data-* attributes of #comments-config, so this file stays plain static JS.
 */
(function () {
  'use strict';

  const config = document.getElementById('comments-config');
  if (!config) {
    return; // include not rendered on this page -> nothing to do
  }

  const SHORTNAME = config.dataset.shortname;
  const PAGE_URL = config.dataset.pageUrl;
  const IDENTIFIER = config.dataset.identifier;

  // Global, read by Disqus' embed.js and DISQUS.reset().
  window.disqus_config = function () {
    this.page.url = PAGE_URL;
    this.page.identifier = IDENTIFIER;
  };

  let commentCount = null; // null = unknown / count unavailable
  let disqusLoaded = false;

  function collapsedLabel() {
    if (commentCount === 0) return 'No comments';
    if (commentCount > 0) return 'Show comments (' + commentCount + ')';
    return 'Show comments'; // count unknown -> no number, still works
  }

  function setLabel(toggle, expanded) {
    const label = toggle.querySelector('.comments-toggle-label');
    if (label) {
      label.textContent = expanded ? 'Hide comments' : collapsedLabel();
    }
  }

  // Fetch the count via Disqus' structured callback, fully guarded.
  function loadCommentCount(toggle) {
    try {
      window.DISQUSWIDGETS = {
        displayCount: function (data) {
          try {
            const entry = data && data.counts && data.counts[0];
            commentCount = entry && typeof entry.comments === 'number' ? entry.comments : null;
            if (toggle.getAttribute('aria-expanded') !== 'true') {
              setLabel(toggle, false);
            }
          } catch (e) {
            // Unexpected payload shape: keep neutral label, never throw.
          }
        },
      };

      const s = document.createElement('script');
      s.src =
        'https://' + SHORTNAME + '.disqus.com/count-data.js?1=' + encodeURIComponent(IDENTIFIER);
      s.async = true;
      // Endpoint 404/network failure -> just keep the neutral label.
      s.onerror = function () {};
      document.head.appendChild(s);
    } catch (e) {
      // Anything unexpected: count stays unknown, toggle still works.
    }
  }

  function loadDisqus(panel) {
    disqusLoaded = true;
    try {
      const s = document.createElement('script');
      s.src = 'https://' + SHORTNAME + '.disqus.com/embed.js';
      s.setAttribute('data-timestamp', +new Date());
      s.onerror = function () {
        disqusLoaded = false; // allow a retry on the next open
        const thread = panel.querySelector('#disqus_thread');
        if (thread) {
          thread.innerHTML =
            '<p class="text-center text-muted small">Comments are currently unavailable.</p>';
        }
      };
      (document.head || document.body).appendChild(s);
    } catch (e) {
      disqusLoaded = false;
    }
  }

  function addDisqus() {
    const footer = document.querySelector('footer');
    if (!footer) {
      return; // nothing to anchor to -> bail out quietly
    }

    const wrapper = document.createElement('div');
    wrapper.id = 'comments-wrapper';
    wrapper.className = 'comments-collapsible';

    const toggle = document.createElement('button');
    toggle.type = 'button';
    toggle.id = 'comments-toggle';
    toggle.className = 'btn btn-outline-secondary btn-sm w-100 comments-toggle';
    toggle.setAttribute('aria-expanded', 'false');
    toggle.setAttribute('aria-controls', 'comments-panel');
    toggle.innerHTML =
      '<i class="far fa-comments fa-fw me-1" aria-hidden="true"></i>' +
      '<span class="comments-toggle-label">Show comments</span>' +
      '<i class="fa-solid fa-angle-down fa-fw ms-1 comments-toggle-icon" aria-hidden="true"></i>';

    const panel = document.createElement('div');
    panel.id = 'comments-panel';
    panel.className = 'comments-panel';
    panel.hidden = true;

    const disqusThread = document.createElement('div');
    disqusThread.id = 'disqus_thread';

    const paragraph = document.createElement('p');
    paragraph.className = 'text-center text-muted small';
    paragraph.innerHTML = 'Comments powered by <a href="https://disqus.com/">Disqus</a>.';
    disqusThread.appendChild(paragraph);

    panel.appendChild(disqusThread);
    wrapper.appendChild(toggle);
    wrapper.appendChild(panel);
    footer.insertAdjacentElement('beforebegin', wrapper);

    loadCommentCount(toggle);

    toggle.addEventListener('click', function () {
      const expanded = toggle.getAttribute('aria-expanded') === 'true';
      const show = !expanded;

      toggle.setAttribute('aria-expanded', String(show));
      panel.hidden = !show;
      setLabel(toggle, show);

      if (show && !disqusLoaded) {
        loadDisqus(panel);
      }
    });
  }

  // Auto switch theme (guarded against the Theme API being absent).
  function reloadDisqus(event) {
    if (typeof Theme === 'undefined') {
      return;
    }
    if (event.source === window && event.data && event.data.id === Theme.eventId) {
      // Disqus hasn't been loaded
      if (typeof DISQUS === 'undefined') {
        return;
      }
      if (document.readyState === 'complete') {
        DISQUS.reset({ reload: true, config: window.disqus_config });
      }
    }
  }

  try {
    addDisqus();
    if (typeof Theme !== 'undefined' && Theme.isToggleable) {
      addEventListener('message', reloadDisqus);
    }
  } catch (e) {
    // Never let comment wiring break the rest of the page.
  }
})();
