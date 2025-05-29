import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
} from '@mui/material';
import axios from 'axios';
import { API_URL } from '../config';

import logo from '../assets/logo.png';

function Login() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        `${API_URL}/login/`,
        formData,
        { headers: { 'Content-Type': 'application/json' } }
      );
      console.log('LOGIN RESPONSE:', response.data);
      // Try all possible token field names
      const token = response.data.token || response.data.key || response.data.access || response.data.auth_token;
      if (token && typeof token === 'string' && token !== 'undefined') {
        localStorage.setItem('token', token);
        if (response.data.username) localStorage.setItem('admin_username', response.data.username);
        if (response.data.email) localStorage.setItem('admin_email', response.data.email);
        console.log('TOKEN GUARDAT:', token);
        console.log('Al local storage:', localStorage);
        axios.defaults.headers.common['Authorization'] = `token ${token}`;
        navigate('/');
      } else {
        setError('No s\'ha pogut obtenir el token d\'autenticació.');
      }
    } catch (error) {
      console.error('Error during login:', error);
      if (error.response) {
        const data = error.response.data;
        if (data.non_field_errors) {
          setError('No s\'ha pogut iniciar sessió: usuari o contrasenya incorrectes.');
        } else if (typeof data === 'string') {
          setError(data);
        } else if (data.detail) {
          setError(data.detail);
        } else {
          setError(JSON.stringify(data));
        }
      } else {
        setError('Error de connexió amb el servidor');
      }
    }
  };

  return (
    <Box
      sx={{
        position: 'relative',
        width: '90vw',
        minHeight: '100vh',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        '&::after': {
          content: '""',
          position: 'absolute',
          inset: 0,
          bgcolor: 'rgba(255,255,255,0.8)',
          zIndex: 1,
        },
      }}
    >
      <div className="login-center-container" style={{ position: 'relative', zIndex: 2 }}>
        <Container maxWidth="xs">
          <Paper sx={{ p: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom align="center">
              Login Administrador
            </Typography>

            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            <Box component="form" onSubmit={handleSubmit}>
              <TextField
                fullWidth
                label="Nombre de usuario"
                name="username"
                value={formData.username}
                onChange={handleChange}
                margin="normal"
                required
              />
              <TextField
                fullWidth
                label="Contraseña"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                margin="normal"
                required
              />
              <Button
                fullWidth
                variant="contained"
                color="primary"
                type="submit"
                sx={{ mt: 3 }}
              >
                Iniciar sesión
              </Button>
            </Box>
          </Paper>
        </Container>
      </div>
    </Box>
  );
}

export default Login;