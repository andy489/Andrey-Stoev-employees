document.addEventListener('DOMContentLoaded', function () {

    if (typeof bootstrap !== 'undefined') {
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }

    makeTablesResponsive();

    initializeTableSort();

    addHoverEffects();

    initializeFileUpload();
});

function makeTablesResponsive() {
    document.querySelectorAll('table').forEach(table => {
        const headers = [];
        table.querySelectorAll('thead th').forEach(header => {
            headers.push(header.textContent);
        });

        table.querySelectorAll('tbody tr').forEach(row => {
            row.querySelectorAll('td').forEach((cell, index) => {
                if (headers[index]) {
                    cell.setAttribute('data-label', headers[index]);
                }
            });
        });
    });
}

function initializeTableSort() {
    document.querySelectorAll('.table th').forEach((header, index) => {
        header.style.cursor = 'pointer';
        header.addEventListener('click', () => {
            sortTable(header.closest('table'), index);
        });

        header.innerHTML += ' <span class="sort-icon">↕️</span>';
    });
}

function sortTable(table, column) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));

    const isNumeric = rows.every(row => {
        const cell = row.querySelectorAll('td')[column];
        return cell && !isNaN(cell.textContent.trim());
    });

    rows.sort((a, b) => {
        const aValue = a.querySelectorAll('td')[column].textContent.trim();
        const bValue = b.querySelectorAll('td')[column].textContent.trim();

        if (isNumeric) {
            return parseFloat(aValue) - parseFloat(bValue);
        }
        return aValue.localeCompare(bValue);
    });

    const currentIcon = table.querySelector(`th:nth-child(${column + 1}) .sort-icon`);
    if (currentIcon.textContent === '↑') {
        rows.reverse();
        currentIcon.textContent = '↓';
    } else {
        currentIcon.textContent = '↑';
    }

    table.querySelectorAll('.sort-icon').forEach((icon, idx) => {
        if (idx !== column) {
            icon.textContent = '↕️';
        }
    });

    tbody.innerHTML = '';
    rows.forEach(row => tbody.appendChild(row));
}

function addHoverEffects() {
    document.querySelectorAll('tbody tr').forEach(row => {
        row.addEventListener('mouseenter', () => {
            row.style.backgroundColor = 'rgba(102, 126, 234, 0.1)';
        });

        row.addEventListener('mouseleave', () => {
            row.style.backgroundColor = '';
        });
    });
}

function initializeFileUpload() {
    const uploadForm = document.querySelector('form[enctype="multipart/form-data"]');
    if (!uploadForm) return;

    uploadForm.addEventListener('submit', function (e) {
        const submitBtn = this.querySelector('button[type="submit"]');
        const fileInput = this.querySelector('input[type="file"]');

        if (fileInput && fileInput.files.length === 0) {
            e.preventDefault();
            showAlert('Please select a file to upload', 'warning');
            return;
        }

        // Show loading state
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="spinner"></span> Processing...';
        }
    });
}

function showAlert(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    const container = document.querySelector('.container');
    if (container) {
        container.insertBefore(alertDiv, container.firstChild);

        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }
}

function formatDate(dateString) {
    if (!dateString || dateString === 'NULL') return 'Present';

    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return dateString;

        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    } catch (e) {
        return dateString;
    }
}

function addCopyButtons() {
    document.querySelectorAll('.pair-section').forEach((section, index) => {
        const header = section.querySelector('.pair-header');
        if (!header) return;

        const copyBtn = document.createElement('button');
        copyBtn.className = 'btn btn-sm btn-outline-secondary ms-2';
        copyBtn.innerHTML = '📋 Copy';
        copyBtn.title = 'Copy pair data to clipboard';

        copyBtn.addEventListener('click', () => {
            const table = section.querySelector('table');
            let text = `Employee Pair: ${section.querySelector('h4').textContent}\n\n`;

            table.querySelectorAll('tbody tr').forEach(row => {
                const cells = row.querySelectorAll('td');
                text += `${cells[2].textContent}\t${cells[3].textContent}\n`;
            });

            navigator.clipboard.writeText(text).then(() => {
                copyBtn.innerHTML = '✅ Copied!';
                setTimeout(() => {
                    copyBtn.innerHTML = '📋 Copy';
                }, 2000);
            });
        });

        header.appendChild(copyBtn);
    });
}

// Search functionality
function initializeSearch() {
    const searchInput = document.createElement('input');
    searchInput.type = 'text';
    searchInput.className = 'form-control mb-3';
    searchInput.placeholder = 'Search by employee ID or project...';

    const container = document.querySelector('.table-container');
    if (container) {
        container.insertBefore(searchInput, container.firstChild);

        searchInput.addEventListener('keyup', function () {
            const searchTerm = this.value.toLowerCase();

            document.querySelectorAll('.pair-section').forEach(section => {
                let showSection = false;

                section.querySelectorAll('tbody tr').forEach(row => {
                    const text = row.textContent.toLowerCase();
                    if (text.includes(searchTerm)) {
                        row.style.display = '';
                        showSection = true;
                    } else {
                        row.style.display = 'none';
                    }
                });

                section.style.display = showSection ? '' : 'none';
            });
        });
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        makeTablesResponsive,
        initializeTableSort,
        addHoverEffects,
        formatDate
    };
}