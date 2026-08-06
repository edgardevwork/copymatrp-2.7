<?php
class Database {
    private $host = '85.193.87.122';
    private $db = 'bdnlremakelauncher';
    private $user = 'bdnlremake';
    private $pass = 'kM2vR8tS0d';
    private $charset = 'utf8mb4';
    private $pdo;

    public function __construct() {
        $dsn = "mysql:host=$this->host;dbname=$this->db;charset=$this->charset";
        $options = [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false,
        ];

        try {
            $this->pdo = new PDO($dsn, $this->user, $this->pass, $options);
        } catch (PDOException $e) {
            throw new PDOException($e->getMessage(), (int)$e->getCode());
        }
    }

    public function getConnection() {
        return $this->pdo;
    }

    public function dispense($table) {
        return new CustomBean($this->pdo, $table);
    }

    public function findOne($table, $condition, $params = []) {
        $stmt = $this->pdo->prepare("SELECT * FROM $table WHERE $condition LIMIT 1");
        $stmt->execute($params);
        return $stmt->fetch();
    }

    public function store($bean) {
        return $bean->save();
    }
}

class CustomBean {
    private $pdo;
    private $table;
    private $data = [];
    private $isNew = true;
    private $idField = 'id';

    public function __construct($pdo, $table) {
        $this->pdo = $pdo;
        $this->table = $table;
    }

    public function __set($name, $value) {
        $this->data[$name] = $value;
    }

    public function __get($name) {
        return $this->data[$name] ?? null;
    }

    public function save() {
        if ($this->isNew) {
            return $this->insert();
        } else {
            return $this->update();
        }
    }

    private function insert() {
        $columns = implode(', ', array_keys($this->data));
        $placeholders = implode(', ', array_fill(0, count($this->data), '?'));
        $values = array_values($this->data);

        $sql = "INSERT INTO {$this->table} ($columns) VALUES ($placeholders)";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($values);
        
        $this->isNew = false;
        return $this->pdo->lastInsertId();
    }

    private function update() {
        if (!isset($this->data[$this->idField])) {
            throw new Exception("ID field is required for update");
        }

        $setParts = [];
        $values = [];
        foreach ($this->data as $key => $value) {
            if ($key !== $this->idField) {
                $setParts[] = "$key = ?";
                $values[] = $value;
            }
        }
        $values[] = $this->data[$this->idField];

        $sql = "UPDATE {$this->table} SET " . implode(', ', $setParts) . " WHERE {$this->idField} = ?";
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($values);
    }
}

// Создаем глобальный экземпляр базы данных
$db = new Database();
?>