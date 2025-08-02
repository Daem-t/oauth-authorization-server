import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { RootState } from '../../app/store';

// This is a simplified DTO, we'll expand it later
export interface Client {
    clientId: string;
    // Add other properties as needed
}

interface ClientsState {
    clients: Client[];
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: string | null;
}

const initialState: ClientsState = {
    clients: [],
    status: 'idle',
    error: null,
};

export const fetchClients = createAsyncThunk('clients/fetchClients', async () => {
    const response = await axios.get('/api/clients');
    return response.data;
});

const clientsSlice = createSlice({
    name: 'clients',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(fetchClients.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(fetchClients.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.clients = action.payload;
            })
            .addCase(fetchClients.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.error.message || 'Something went wrong';
            });
    },
});

export const selectAllClients = (state: RootState) => state.clients.clients;
export const getClientsStatus = (state: RootState) => state.clients.status;

export default clientsSlice.reducer;
