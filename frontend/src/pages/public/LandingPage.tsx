import { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import './LandingPage.css';

const LOCAL_VIDEO = '/videos/kingfisher-hero.mp4';
const REMOTE_VIDEO = 'https://thinkingods.com/demos/kingfisher-hero/hero.mp4';
const REVEAL_TRIGGER_TIME = 4.3; // seconds
const FALLBACK_TIMEOUT_MS = 5000;

export default function LandingPage() {
  const [videoSrc, setVideoSrc] = useState<string>(LOCAL_VIDEO);
  const [isRevealed, setIsRevealed] = useState<boolean>(false);
  const [videoFailed, setVideoFailed] = useState<boolean>(false);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const hasTriggeredRef = useRef<boolean>(false);

  const triggerReveal = () => {
    if (!hasTriggeredRef.current) {
      hasTriggeredRef.current = true;
      setIsRevealed(true);
    }
  };

  useEffect(() => {
    // Check for reduced motion preference
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion) {
      triggerReveal();
      return;
    }

    // Safety fallback timeout: never leave content hidden
    const timer = setTimeout(() => {
      triggerReveal();
    }, FALLBACK_TIMEOUT_MS);

    return () => clearTimeout(timer);
  }, []);

  const handleTimeUpdate = () => {
    const video = videoRef.current;
    if (video && video.currentTime >= REVEAL_TRIGGER_TIME) {
      triggerReveal();
    }
  };

  const handleVideoError = () => {
    if (videoSrc === LOCAL_VIDEO) {
      // Switch to remote fallback
      setVideoSrc(REMOTE_VIDEO);
    } else {
      // Both sources failed, reveal content immediately with sage-green fallback
      setVideoFailed(true);
      triggerReveal();
    }
  };

  const handleVideoEnded = () => {
    triggerReveal();
    // Hold on final frame by pausing
    if (videoRef.current) {
      videoRef.current.pause();
    }
  };

  return (
    <div className="landing-container">
      {/* ── Hero Viewport ── */}
      <section className="landing-hero" aria-label="Hero Section">
        {!videoFailed && (
          <div className="hero-video-wrapper">
            <video
              ref={videoRef}
              className="hero-video"
              src={videoSrc}
              muted
              autoPlay
              playsInline
              onTimeUpdate={handleTimeUpdate}
              onError={handleVideoError}
              onEnded={handleVideoEnded}
              aria-hidden="true"
            />
            <div className="hero-overlay" />
          </div>
        )}

        {/* ── Top Navigation ── */}
        <header className="landing-nav">
          <Link to="/" className="landing-brand">
            <div className="landing-logo-badge">E</div>
            <div>
              <span className="landing-logo-text">EQEL</span>
              <span className="landing-tagline">Campus Placement · Decision Intelligence</span>
            </div>
          </Link>

          <div className="landing-nav-actions">
            <Link to="/roles" className="btn btn-outline btn-sm">
              Role Portals
            </Link>
            <Link to="/roles" className="btn btn-primary btn-sm">
              Sign In →
            </Link>
          </div>
        </header>

        {/* ── Main Hero Editorial Content ── */}
        <div className={`hero-content ${isRevealed ? 'revealed' : ''}`}>
          <div className="hero-left">
            <div className="hero-eyebrow">
              <span className="badge badge-eligible">Live</span>
              Campus Placement · Decision Intelligence
            </div>

            <h1 className="hero-headline">
              Eligibility decisions you can <em>explain.</em>
            </h1>

            <p className="hero-subheading">
              Evaluate student evidence against versioned placement policies, understand the reasoning behind every outcome, and identify cases requiring further verification.
            </p>

            <div className="hero-actions">
              <Link to="/roles" className="hero-login-btn">
                LOGIN →
              </Link>
              <a href="#how-it-works" className="btn btn-outline">
                Explore How It Works
              </a>
            </div>
          </div>

          {/* ── Restrained Dark-Green Floating Panels (Illustrative) ── */}
          <div className="hero-right">
            {/* Panel 1: Eligibility Engine */}
            <div className="floating-panel">
              <div className="floating-panel-header">
                <span className="floating-panel-title">ELIGIBILITY ENGINE</span>
                <span className="panel-demo-label">SIMULATED EVALUATION</span>
              </div>

              <div className="panel-rule-item">
                <span className="panel-rule-code">RULE-01: CGPA ≥ 7.50</span>
                <span className="badge badge-eligible">Satisfied (8.42)</span>
              </div>
              <div className="panel-rule-item">
                <span className="panel-rule-code">RULE-02: Backlogs ≤ 0</span>
                <span className="badge badge-eligible">Satisfied (0)</span>
              </div>
              <div className="panel-rule-item">
                <span className="panel-rule-code">RULE-03: Dept IN [CSE, IT]</span>
                <span className="badge badge-eligible">Satisfied (CSE)</span>
              </div>

              <div className="panel-outcome-bar">
                <span className="panel-outcome-tag">VERDICT: ELIGIBLE</span>
                <span className="panel-outcome-note">All 3 mandatory rules met</span>
              </div>
            </div>

            {/* Panel 2: Evidence Review */}
            <div className="floating-panel">
              <div className="floating-panel-header">
                <span className="floating-panel-title">EVIDENCE REVIEW</span>
                <span className="panel-demo-label">RESOLVE CONFLICTS</span>
              </div>

              <div className="panel-rule-item">
                <span className="panel-rule-code">RULE-04: Degree Transcript</span>
                <span className="badge badge-unverifiable">Unverified</span>
              </div>
              <div className="panel-rule-item">
                <span className="panel-rule-code">POLICY: Google SWE 2026 (v2)</span>
                <span className="badge badge-verified">Immutable Snapshot</span>
              </div>

              <div className="panel-outcome-bar" style={{ background: 'rgba(212, 146, 10, 0.15)', borderColor: 'rgba(212, 146, 10, 0.35)' }}>
                <span className="panel-outcome-tag" style={{ color: '#F3A15E' }}>RESOLUTION REQUIRED</span>
                <span className="panel-outcome-note">Awaiting officer sign-off</span>
              </div>
            </div>
          </div>
        </div>

        {/* ── Bottom Indicator ── */}
        <div style={{ textAlign: 'center', zIndex: 10, opacity: isRevealed ? 0.7 : 0, transition: 'opacity 0.5s' }}>
          <a href="#how-it-works" style={{ color: 'var(--ink-3)', fontSize: '0.8125rem', fontFamily: 'var(--font-mono)' }}>
            ↓ SCROLL TO EXPLORE ARCHITECTURE
          </a>
        </div>
      </section>

      {/* ── Below the Fold: 4 Stages ── */}
      <section id="how-it-works" className="landing-stages-section">
        <div className="stages-inner">
          <div className="stages-header">
            <div className="stages-eyebrow">The EQEL Methodology</div>
            <h2 className="stages-title">From evidence to an explainable decision.</h2>
            <p className="stages-subtitle">
              Every placement decision is backed by admissible academic proof, locked policy versions, and audit provenance.
            </p>
          </div>

          <div className="stages-grid">
            <div className="stage-card">
              <div className="stage-number">01</div>
              <h3 className="stage-card-title">Evidence</h3>
              <p className="stage-card-text">
                Academic records, transcripts, CGPA claims, and backlog counts are collected with explicit source verification and revision tracking.
              </p>
            </div>

            <div className="stage-card">
              <div className="stage-number">02</div>
              <h3 className="stage-card-title">Policy</h3>
              <p className="stage-card-text">
                Placement officers define corporate drive criteria as immutable, versioned rules. Changes create new versions without mutating history.
              </p>
            </div>

            <div className="stage-card">
              <div className="stage-number">03</div>
              <h3 className="stage-card-title">Decision</h3>
              <p className="stage-card-text">
                The deterministic engine produces one of three outcomes: Eligible, Ineligible, or Unverifiable, detailing exact rule satisfaction.
              </p>
            </div>

            <div className="stage-card">
              <div className="stage-number">04</div>
              <h3 className="stage-card-title">Provenance</h3>
              <p className="stage-card-text">
                All decisions preserve the exact policy version, student evidence snapshot, and engine version for full audit replayability.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* ── Final Call to Action ── */}
      <section className="landing-final-cta">
        <div className="final-cta-inner">
          <h2 className="final-cta-title">Ready to streamline campus recruitment?</h2>
          <p className="final-cta-text">
            Log in to your dedicated portal as an Administrator, Placement Officer, or Student.
          </p>
          <Link to="/roles" className="btn btn-primary btn-lg">
            Choose Your Portal →
          </Link>
        </div>
      </section>
    </div>
  );
}
