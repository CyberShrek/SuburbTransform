SELECT id FROM nglog.processes
          WHERE system_n = ${systemName}
          ORDER BY id DESC LIMIT 1