// src/main/webapp/components/common/Pagination.tsx
import React from 'react';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({ currentPage, totalPages, onPageChange }) => {
  if (totalPages <= 1) {
    return null;
  }

  const handlePrevious = () => {
    if (currentPage > 0) {
      onPageChange(currentPage - 1);
    }
  };

  const handleNext = () => {
    if (currentPage < totalPages - 1) {
      onPageChange(currentPage + 1);
    }
  };

  return (
    <nav aria-label="Page navigation">
      <ul className="pagination justify-content-center">
        <li className={`page-item ${currentPage === 0 ? 'disabled' : ''}`}>
          <button className="page-link" onClick={handlePrevious} aria-label="Previous">
            &laquo;
          </button>
        </li>
        {[...Array(totalPages).keys()].map(pageNumber => (
          <li key={pageNumber} className={`page-item ${currentPage === pageNumber ? 'active' : ''}`}>
            <button className="page-link" onClick={() => onPageChange(pageNumber)}>
              {pageNumber + 1}
            </button>
          </li>
        ))}
        <li className={`page-item ${currentPage >= totalPages - 1 ? 'disabled' : ''}`}>
          <button className="page-link" onClick={handleNext} aria-label="Next">
            &raquo;
          </button>
        </li>
      </ul>
    </nav>
  );
};

export default Pagination;
