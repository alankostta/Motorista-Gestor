INSERT INTO tb_role (role_id, nome_role) VALUES
  (1, 'ADMIN'),
  (2, 'MOTORISTA')
ON CONFLICT (role_id) DO NOTHING;


		