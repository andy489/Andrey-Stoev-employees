// Toggle debug information
function toggleDebug() {
    const debugContent = document.getElementById('debugContent');
    if (debugContent.style.display === 'none' || debugContent.style.display === '') {
        debugContent.style.display = 'block';
    } else {
        debugContent.style.display = 'none';
    }
}

// Log error to console for debugging
console.error('Error Page Loaded:', {
    errorId: document.querySelector('.error-id span')?.textContent,
    title: document.querySelector('.error-header h1')?.textContent,
    message: document.querySelector('.error-header p')?.textContent,
    url: window.location.href,
    timestamp: new Date().toISOString()
});

// Add easter egg - click on error header 3 times
let clickCount = 0;
const errorHeader = document.querySelector('.error-header');
if (errorHeader) {
    errorHeader.addEventListener('click', function () {
        clickCount++;
        if (clickCount === 3) {
            alert('🦄 You found the secret unicorn! Error handling is magical!\n\nError ID: ' +
                (document.querySelector('.error-id span')?.textContent || 'unknown'));
            this.style.background = 'linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%)';
            clickCount = 0;

            // Create confetti effect
            createConfetti();
        }
    });
}

// Simple confetti effect
function createConfetti() {
    for (let i = 0; i < 50; i++) {
        const confetti = document.createElement('div');
        confetti.style.position = 'fixed';
        confetti.style.left = Math.random() * 100 + '%';
        confetti.style.top = '-10px';
        confetti.style.width = '8px';
        confetti.style.height = '8px';
        confetti.style.backgroundColor = `hsl(${Math.random() * 360}, 100%, 50%)`;
        confetti.style.borderRadius = '50%';
        confetti.style.pointerEvents = 'none';
        confetti.style.zIndex = '9999';
        confetti.style.animation = `fall ${Math.random() * 3 + 2}s linear`;
        document.body.appendChild(confetti);

        setTimeout(() => confetti.remove(), 5000);
    }
}

// Add animation keyframes
const style = document.createElement('style');
style.textContent = `
            @keyframes fall {
                to {
                    transform: translateY(100vh) rotate(360deg);
                }
            }
        `;
document.head.appendChild(style);

// Auto-hide debug info after 30 seconds if visible
setTimeout(() => {
    const debugContent = document.getElementById('debugContent');
    if (debugContent && debugContent.style.display === 'block') {
        debugContent.style.display = 'none';
    }
}, 30000);

// Decode and display URL information
const urlParams = new URLSearchParams(window.location.search);
console.log('URL Parameters:', Object.fromEntries(urlParams.entries()));

// Special handling for Cyrillic URLs
if (window.location.pathname.includes('%D0%B0%D1%81%D0%B4')) {
    console.log('Cyrillic URL detected: "асп"');
    document.body.classList.add('cyrillic-mode');
}