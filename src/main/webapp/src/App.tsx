import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';

const darkTheme = createTheme({
    palette: {
        mode: 'dark',
    },
});

const App: React.FC = () => {
    return (
        <ThemeProvider theme={darkTheme}>
            <CssBaseline />
            <Router>
                <Container component="main" sx={{ mt: 8 }}>
                    <Typography variant="h2" component="h1" gutterBottom>
                        OAuth2 Admin
                    </Typography>
                    <Routes>
                        <Route path="/" element={<div>Home</div>} />
                    </Routes>
                </Container>
            </Router>
        </ThemeProvider>
    );
}

export default App;