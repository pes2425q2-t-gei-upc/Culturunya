// src/components/UserDetail.jsx
import { useLocation, useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  Button,
  Divider,
  IconButton,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Avatar,
  Alert,
  Stack,
  Select,
  MenuItem,
  TextField,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import PersonIcon from '@mui/icons-material/Person';
import { useState, useEffect } from 'react';
import axios from 'axios';
import { API_URL } from '../config';
import logo from '../assets/logo.png';

const editableFields = [
  'username',
  'email',
  'phone_number',
  'birth_date',
  'language',
  'fullname',
  'banned_from_comments',
];

const hiddenFields = ['profile_pic', 'fullname', 'first_name', 'last_name', 'rank_event', 'rank_quiz', 'total_event_points', 'total_quiz_points'];

export default function UserDetail() {
  const location = useLocation();
  const navigate = useNavigate();
  const [user, setUser] = useState(location.state?.user);
  const [openDelete, setOpenDelete] = useState(false);
  const [editField, setEditField] = useState(null);
  const [editValue, setEditValue] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showMainSuccess, setShowMainSuccess] = useState(false);

  useEffect(() => {
    // Carrega l'usuari sempre que el component es munta o canvia l'id
    const userId = location.state?.user?.id;
    if (userId) {
      fetchUser(userId);
    }
    // eslint-disable-next-line
  }, [location.state?.user?.id]);

  if (!user) {
    return (
      <Container maxWidth="sm" sx={{ mt: 4 }}>
        <Paper sx={{ p: 3 }}>
          <Typography variant="h6">No s'ha trobat la informació de l'usuari.</Typography>
          <Button variant="outlined" sx={{ mt: 2 }} onClick={() => navigate(-1)}>
            Tornar enrere
          </Button>
        </Paper>
      </Container>
    );
  }

  // Funció per recarregar l'usuari des del backend
  const fetchUser = async (userId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${API_URL}/admin/get_user/${userId}/`, {
        headers: { Authorization: `token ${token}` },
      });
      setUser(response.data);
    } catch (err) {
      setError('No s\'han pogut refrescar les dades de l\'usuari.');
    }
  };

  // Nova funció per refrescar l'usuari a partir de la llista
  const refreshUserFromList = async (userId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${API_URL}/admin/get_users/`, {
        headers: { Authorization: `token ${token}` },
      });
      const found = response.data.find(u => u.id === userId);
      if (found) setUser(found);
    } catch (err) {
      setError('No s\'han pogut refrescar les dades de l\'usuari.');
    }
  };

  const handleDelete = async () => {
    try {
      const token = localStorage.getItem('token');
      await axios.delete(`${API_URL}/admin/admin_delete_user_account/${user.id}/`, {
        headers: { Authorization: `token ${token}` },
      });
      setSuccess('Compte eliminat correctament.');
      setTimeout(() => navigate('/'), 1200);
    } catch (err) {
      if (err.response) {
        if (err.response.status === 401) {
          setError('No autenticat. Torna a iniciar sessió.');
        } else if (err.response.status === 403) {
          setError('No autoritzat: només els administradors poden eliminar usuaris.');
        } else if (err.response.status === 404) {
          setError('Usuari no trobat.');
        } else {
          setError('Error inesperat: ' + (err.response?.data?.detail || JSON.stringify(err.response?.data)));
        }
      } else {
        setError('Error de connexió amb el servidor.');
      }
    }
  };

  const handleEdit = (field, value) => {
    setEditField(field);
    setEditValue(value || '');
    setError('');
    setSuccess('');
  };

  const handleEditSave = async () => {
    try {
      const token = localStorage.getItem('token');
      let valueToSend = editValue;
      await axios.put(
        `${API_URL}/admin/update_user_partial/${user.id}/`,
        { [editField]: valueToSend },
        { headers: { Authorization: `token ${token}` } }
      );
      setEditField(null);
      setShowMainSuccess(true);
      await refreshUserFromList(user.id);
      setTimeout(() => setShowMainSuccess(false), 2500);
    } catch (err) {
      if (err.response) {
        if (err.response.status === 400) {
          setError('Dades invàlides. Revisa el valor introduït.');
        } else if (err.response.status === 403) {
          setError('No autoritzat: només els administradors poden modificar aquest camp.');
        } else if (err.response.status === 404) {
          setError('Usuari no trobat.');
        } else {
          setError('Error inesperat: ' + (err.response?.data?.detail || JSON.stringify(err.response?.data)));
        }
      } else {
        setError('Error de connexió amb el servidor.');
      }
    }
  };

  // Order of fields to display
  const orderedFields = [
    'id',
    'username',
    'email',
    'password',
    'phone_number',
    'birth_date',
    'banned_from_comments',
    'language',
    // is_admin will be shown at the end
  ];

  return (
    <Container
      maxWidth="xl"
      sx={{
        mt: 4,
        mb: 4,
        minHeight: '80vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      {showMainSuccess && (
        <Alert severity="success" sx={{ mb: 2, position: 'fixed', top: 80, left: 0, right: 0, zIndex: 9999, width: 'fit-content', mx: 'auto' }}>
          Camp actualitzat correctament!
        </Alert>
      )}
      <Box
        sx={{
          position: 'relative',
          width: '70vw',
          maxWidth: 900,
          borderRadius: 4,
          overflow: 'hidden',
          backgroundImage: `url(${logo})`,
          backgroundSize: 'contain',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center',
          '&::after': {
            content: '""',
            position: 'absolute',
            inset: 0,
            bgcolor: 'rgba(255,255,255,0)',
            zIndex: 1,
          },
        }}
      >
        <Paper
          sx={{
            p: { xs: 2, sm: 4 },
            position: 'relative',
            boxShadow: 4,
            borderRadius: 4,
            background: 'rgba(255,255,255,0.9)',
            width: '100%',
            maxWidth: 900,
            mx: 'auto',
            zIndex: 2,
          }}
        >
          <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1, justifyContent: 'center' }}>
            <Box sx={{ position: 'relative', display: 'inline-block' }}>
              <Avatar sx={{ width: 120, height: 120, fontSize: 50 }} src={user.profile_pic || undefined}>
                <PersonIcon fontSize="large" />
              </Avatar>
              <Tooltip title="Canviar foto de perfil">
                <IconButton
                  size="large"
                  sx={{
                    position: 'absolute',
                    bottom: 8,
                    right: 8,
                    bgcolor: 'white',
                    boxShadow: 2,
                    '&:hover': { bgcolor: 'grey.100' },
                    p: 0.5,
                  }}
                >
                  <EditIcon fontSize="medium" />
                </IconButton>
              </Tooltip>
            </Box>
            <Box>
              <Typography variant="h4" gutterBottom>
                {user.username}
              </Typography>
            </Box>
            <Box sx={{ flex: 1 }} />
            <Tooltip title="Eliminar usuari">
              <IconButton color="error" onClick={() => setOpenDelete(true)} size="large">
                <DeleteIcon fontSize="large" />
              </IconButton>
            </Tooltip>
          </Stack>
          <Divider sx={{ mb: 2 }} />
          <Stack spacing={2}>
            {orderedFields.map((key) => {
              if (key === 'password') {
                return (
                  <Box key="password" sx={{ display: 'flex', alignItems: 'center', py: 2, borderBottom: '1px solid #f0f0f0', minHeight: 56 }}>
                    <Typography variant="h6" color="text.secondary" sx={{ minWidth: 180, flex: 1, fontSize: 20, textAlign: 'left', pr: 4 }}>
                      password
                    </Typography>
                    <Typography variant="h5" sx={{ flex: 2, wordBreak: 'break-all', fontSize: 22, textAlign: 'left', pl: 0, ml: 0 }}>
                      ********
                    </Typography>
                    <Tooltip title="Canviar contrasenya">
                      <IconButton size="medium" sx={{ ml: 0.5 }} onClick={() => navigate(`/user/${user.username}/change-password`, { state: { user_id: user.id, username: user.username } })}>
                        <EditIcon fontSize="medium" />
                      </IconButton>
                    </Tooltip>
                  </Box>
                );
              }
              if (user[key] === undefined || hiddenFields.includes(key)) return null;
              // Special case for language
              if (key === 'language') {
                return (
                  <Box key={key} sx={{ display: 'flex', alignItems: 'center', py: 2, borderBottom: '1px solid #f0f0f0', minHeight: 56 }}>
                    <Typography variant="h6" color="text.secondary" sx={{ minWidth: 180, flex: 1, fontSize: 20, textAlign: 'left', pr: 4 }}>
                      {key}
                    </Typography>
                    <Typography variant="h5" sx={{ flex: 2, wordBreak: 'break-all', fontSize: 22, textAlign: 'left', pl: 0, ml: 0 }}>
                      {user.language === 'EN' ? 'EN' : 'ES'}
                    </Typography>
                    <Tooltip title="Editar language">
                      <IconButton size="medium" sx={{ ml: 0.5 }} onClick={() => handleEdit('language', user.language)}>
                        <EditIcon fontSize="medium" />
                      </IconButton>
                    </Tooltip>
                  </Box>
                );
              }
              // Special case for banned_from_comments
              if (key === 'banned_from_comments') {
                return (
                  <Box key={key} sx={{ display: 'flex', alignItems: 'center', py: 2, borderBottom: '1px solid #f0f0f0', minHeight: 56 }}>
                    <Typography variant="h6" color="text.secondary" sx={{ minWidth: 180, flex: 1, fontSize: 20, textAlign: 'left', pr: 4 }}>
                      {key}
                    </Typography>
                    <Typography variant="h5" sx={{ flex: 2, wordBreak: 'break-all', fontSize: 22, textAlign: 'left', pl: 0, ml: 0 }}>
                      {user.banned_from_comments ? 'true' : 'false'}
                    </Typography>
                    <Tooltip title="Editar banned_from_comments">
                      <IconButton size="medium" sx={{ ml: 0.5 }} onClick={() => handleEdit('banned_from_comments', user.banned_from_comments)}>
                        <EditIcon fontSize="medium" />
                      </IconButton>
                    </Tooltip>
                  </Box>
                );
              }
              return (
                <Box key={key} sx={{ display: 'flex', alignItems: 'center', py: 2, borderBottom: '1px solid #f0f0f0', minHeight: 56 }}>
                  <Typography variant="h6" color="text.secondary" sx={{ minWidth: 180, flex: 1, fontSize: 20, textAlign: 'left', pr: 4 }}>
                    {key}
                  </Typography>
                  <Typography variant="h5" sx={{ flex: 2, wordBreak: 'break-all', fontSize: 22, textAlign: 'left', pl: 0, ml: 0 }}>
                    {user[key] === null || user[key] === '' ? '-' : user[key].toString()}
                  </Typography>
                  {editableFields.includes(key) && (
                    <Tooltip title={`Editar ${key}`}>
                      <IconButton size="medium" sx={{ ml: 0.5 }} onClick={() => handleEdit(key, user[key])}>
                        <EditIcon fontSize="medium" />
                      </IconButton>
                    </Tooltip>
                  )}
                </Box>
              );
            })}
            {/* is_admin at the end, not editable */}
            {'is_admin' in user && (
              <Box key="is_admin" sx={{ display: 'flex', alignItems: 'center', py: 2, borderBottom: '1px solid #f0f0f0', minHeight: 56 }}>
                <Typography variant="h6" color="text.secondary" sx={{ minWidth: 180, flex: 1, fontSize: 20, textAlign: 'left', pr: 4 }}>
                  is_admin
                </Typography>
                <Typography variant="h5" sx={{ flex: 2, wordBreak: 'break-all', fontSize: 22, textAlign: 'left', pl: 0, ml: 0 }}>
                  {user.is_admin ? 'true' : 'false'}
                </Typography>
              </Box>
            )}
          </Stack>
        </Paper>
      </Box>

      {/* Delete dialog */}
      <Dialog open={openDelete} onClose={() => setOpenDelete(false)}>
        <DialogTitle>Eliminar usuari</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Segur que vols eliminar l'usuari <b>{user.username}</b>? Aquesta acció no es pot desfer.
          </DialogContentText>
          {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mt: 2 }}>{success}</Alert>}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDelete(false)}>Cancel·lar</Button>
          <Button color="error" onClick={handleDelete}>Eliminar</Button>
        </DialogActions>
      </Dialog>

      {/* Edit dialog */}
      <Dialog open={!!editField} onClose={() => setEditField(null)}>
        <DialogTitle>Editar {editField}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Introdueix el nou valor per <b>{editField}</b>:
          </DialogContentText>
          <Box component="form" onSubmit={(e) => { e.preventDefault(); handleEditSave(); }}>
            {editField === 'language' ? (
              <Select
                value={editValue}
                onChange={e => setEditValue(e.target.value)}
                fullWidth
                sx={{ mt: 2 }}
              >
                <MenuItem value="ES">ES</MenuItem>
                <MenuItem value="EN">EN</MenuItem>
              </Select>
            ) : editField === 'banned_from_comments' ? (
              <Select
                value={editValue ? 'true' : 'false'}
                onChange={e => setEditValue(e.target.value === 'true')}
                fullWidth
                sx={{ mt: 2 }}
              >
                <MenuItem value="true">true</MenuItem>
                <MenuItem value="false">false</MenuItem>
              </Select>
            ) : editField === 'birth_date' ? (
              <TextField
                type="date"
                value={editValue ? (() => {
                  // Convert DD-MM-YYYY or YYYY-MM-DD to YYYY-MM-DD for input
                  if (editValue.includes('-') && editValue.split('-')[0].length === 4) return editValue;
                  const [dd, mm, yyyy] = editValue.split('-');
                  return `${yyyy}-${mm}-${dd}`;
                })() : ''}
                onChange={e => setEditValue(e.target.value)}
                size="small"
                sx={{ minWidth: 180 }}
              />
            ) : (
              <input
                autoFocus
                style={{ width: '100%', marginTop: 12, fontSize: 16, padding: 6 }}
                value={editValue}
                onChange={(e) => setEditValue(e.target.value)}
              />
            )}
          </Box>
          {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mt: 2 }}>{success}</Alert>}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditField(null)}>Cancel·lar</Button>
          <Button type="submit" onClick={handleEditSave}>Desar</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}
