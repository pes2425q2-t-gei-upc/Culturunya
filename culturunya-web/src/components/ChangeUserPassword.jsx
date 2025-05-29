import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Container, Paper, Typography, TextField, Button, Alert, Box } from '@mui/material';
import axios from 'axios';
import { API_URL } from '../config';

function ChangeUserPassword() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user_id, username } = location.state || {};
  const [newPassword, setNewPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const adminUsername = localStorage.getItem('admin_username');
  const adminEmail = localStorage.getItem('admin_email');

  if (!user_id) {
    return (
      <Container maxWidth="sm" sx={{ mt: 4 }}>
        <Paper sx={{ p: 3 }}>
          <Typography variant="h6">No s'ha trobat l'usuari.</Typography>
          <Button variant="outlined" sx={{ mt: 2 }} onClick={() => navigate(-1)}>
            Tornar enrere
          </Button>
        </Paper>
      </Container>
    );
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      await axios.put(
        `${API_URL}/admin/change_user_password/`,
        { user_id, new_password: newPassword },
        {
          headers: {
            'Authorization': `token ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      setSuccess('Contrasenya canviada correctament!');
      setNewPassword('');
    } catch (err) {
      if (err.response) {
        if (err.response.status === 400) {
          setError('Dades invàlides. La contrasenya ha de tenir almenys 8 caràcters.');
        } else if (err.response.status === 403) {
          setError('No tens permisos per fer aquest canvi (només admins).');
        } else if (err.response.status === 404) {
          setError('Usuari no trobat.');
        } else {
          setError('Error inesperat: ' + (err.response?.data?.detail || JSON.stringify(err.response?.data)));
        }
      } else {
        setError('Error de connexió amb el servidor.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>Canviar contrasenya d&apos;usuari</Typography>
        <Typography variant="subtitle1" sx={{ mb: 2 }}>Usuari: <b>{username}</b></Typography>
        <Typography variant="subtitle2" sx={{ mb: 1, color: 'text.secondary' }}>
          Acció feta per: <b>{adminUsername || adminEmail || 'Administrador'}</b>
        </Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
        <Box component="form" onSubmit={handleSubmit}>
          <TextField
            label="Nova contrasenya"
            name="new_password"
            type="password"
            value={newPassword}
            onChange={e => setNewPassword(e.target.value)}
            fullWidth
            margin="normal"
            required
          />
          <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
            <Button type="submit" variant="contained" color="primary" disabled={loading}>
              Canviar contrasenya
            </Button>
            <Button variant="outlined" onClick={() => navigate(-1)}>
              Tornar enrere
            </Button>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
}

export default ChangeUserPassword; 