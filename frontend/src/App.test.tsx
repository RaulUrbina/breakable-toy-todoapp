import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import App from './App';

describe('App', () => {
  it('renders without crashing', () => {
    render(<App />);
    // The Card component should be present
    expect(screen.getByRole('search')).toBeInTheDocument();
  });

  it('renders main components', () => {
    render(<App />);
    
    // Check if main components are rendered
    screen.getByPlaceholderText('Filter by task name...')
    expect(screen.getByRole('button', { name: /new to do/i })).toBeInTheDocument(); // AddItemButton
    expect(screen.getByRole('navigation')).toBeInTheDocument(); // PaginationControl
  });
}); 