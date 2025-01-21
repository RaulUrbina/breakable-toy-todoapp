import { describe, it, expect, beforeEach } from 'vitest';
import useModal from './useModal';
import { ToDo } from '@/interfaces/ToDo';

describe('useModal', () => {
  beforeEach(() => {
    useModal.setState({
      isOpen: false,
      dialogType: null,
      data: null,
    });
  });

  it('should initialize with default values', () => {
    const state = useModal.getState();
    expect(state.isOpen).toBe(false);
    expect(state.dialogType).toBeNull();
    expect(state.data).toBeNull();
  });

  it('should open modal with correct type', () => {
    const { onOpen } = useModal.getState();
    onOpen('create');
    
    const state = useModal.getState();
    expect(state.isOpen).toBe(true);
    expect(state.dialogType).toBe('create');
  });

  it('should close modal and reset values', () => {
    // First open the modal
    useModal.getState().onOpen('edit');
    
    // Then close it
    const { onClose } = useModal.getState();
    onClose();
    
    const state = useModal.getState();
    expect(state.isOpen).toBe(false);
    expect(state.dialogType).toBeNull();
    expect(state.data).toBeNull();
  });

  it('should set data correctly', () => {
    const mockTodo: ToDo = {
      id: '1',
      text: 'Test Todo',
      priority: 'HIGH',
      done: false,
      creationDate: new Date().toISOString(),
    };

    const { setData } = useModal.getState();
    setData(mockTodo);
    
    const state = useModal.getState();
    expect(state.data).toEqual(mockTodo);
  });
}); 