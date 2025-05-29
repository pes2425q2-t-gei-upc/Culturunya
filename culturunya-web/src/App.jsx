import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import UserList from './components/UserList';
import UserDetail from './components/UserDetail';
import Login from './components/Login';
import ProtectedRoute from './components/ProtectedRoute';
import CreateUser from './components/CreateUser';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import logo from './assets/logo.png';
import logoSenseFons from './assets/logo_sense_fons.png';
import ChangeUserPassword from './components/ChangeUserPassword';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppBar position="static" color="transparent" elevation={0} sx={{ mb: 2 }}>
        <Toolbar>
          <Box component="img" src={logo} alt="Logo" sx={{ height: 48, mr: 2 }} />
          <Typography variant="h5" color="primary">Culturunya Admin</Typography>
        </Toolbar>
      </AppBar>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <UserList />
              </ProtectedRoute>
            }
          />
          <Route
            path="/user/:id"
            element={
              <ProtectedRoute>
                <UserDetail />
              </ProtectedRoute>
            }
          />
          <Route path="/create-user" element={<ProtectedRoute><CreateUser /></ProtectedRoute>} />
          <Route path="/user/:username/change-password" element={<ProtectedRoute><ChangeUserPassword /></ProtectedRoute>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;
