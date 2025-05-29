import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  Button,
  IconButton,
  Alert,
  Box,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import axios from 'axios';
import { API_URL } from '../config';

function UserList() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const token = localStorage.getItem('token');
      console.log('TOKEN QUE S\'ENVIA:', token);
      if (!token) {
        navigate('/login');
        return;
      }

      const response = await axios.get(`${API_URL}/admin/get_users/`, {
        headers: {
          'Authorization': `token ${token}`
        }
      });
      setUsers(response.data);
      setError('');
    } catch (error) {
      if (error.response && error.response.status === 401) {
        localStorage.removeItem('token');
        window.location.href = '/login';
        return;
      }
      console.error('Error fetching users:', error);
      if (error.response) {
        setError(
          typeof error.response.data === 'string'
            ? error.response.data
            : JSON.stringify(error.response.data)
        );
      } else {
        setError('Error al cargar los usuarios');
      }
    }
  };

  const handleDelete = async (userId) => {
    if (window.confirm('¿Estás seguro de que quieres eliminar este usuario?')) {
      try {
        const token = localStorage.getItem('token');
        await axios.delete(`${API_URL}/admin/users/${userId}/`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        fetchUsers();
      } catch (error) {
        console.error('Error deleting user:', error);
        setError('Error al eliminar el usuario');
      }
    }
  };

  function getUserActions(username) {
    const actions = JSON.parse(localStorage.getItem('userActions') || '{}');
    return actions[username] || 0;
  }

  function incrementUserActions(username) {
    const actions = JSON.parse(localStorage.getItem('userActions') || '{}');
    actions[username] = (actions[username] || 0) + 1;
    localStorage.setItem('userActions', JSON.stringify(actions));
  }

  const sortedUsers = [...users].sort((a, b) => a.id - b.id);

  return (
    <Box
      sx={{
        position: 'relative',
        width: '100%',
        minHeight: '80vh',
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
      <Container maxWidth="lg" sx={{ position: 'relative', zIndex: 2, pt: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Gestión de Usuarios
        </Typography>
        <Button variant="contained" color="primary" sx={{ mb: 2 }} onClick={() => navigate('/create-user')}>
          Crear usuari
        </Button>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Nombre</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Nombre completo</TableCell>
                <TableCell>Teléfono</TableCell>
                <TableCell>Puntos Eventos</TableCell>
                <TableCell>Puntos Quiz</TableCell>
                <TableCell>Acciones Recientes</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {sortedUsers.map((user) => (
                <TableRow key={user.username} hover style={{ cursor: 'pointer' }}
                  onClick={() => {
                    incrementUserActions(user.username);
                    navigate(`/user/${user.username}`, { state: { user } });
                  }}>
                  <TableCell>{user.id}</TableCell>
                  <TableCell>{user.username}</TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>{user.fullname}</TableCell>
                  <TableCell>{user.phone_number}</TableCell>
                  <TableCell>{user.total_event_points}</TableCell>
                  <TableCell>{user.total_quiz_points}</TableCell>
                  <TableCell>{getUserActions(user.username)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Container>
    </Box>
  );
}

export default UserList; 